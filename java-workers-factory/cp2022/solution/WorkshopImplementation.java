package cp2022.solution;

import cp2022.base.Workplace;
import cp2022.base.WorkplaceId;
import cp2022.base.Workshop;

import javax.management.InstanceNotFoundException;
import java.util.*;
import java.util.concurrent.*;

public class WorkshopImplementation implements Workshop {
    private final HashMap<WorkplaceId, Workplace> workplaces = new HashMap<>();
    private final ConcurrentHashMap<Long, WorkplaceId> threads = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Semaphore> waiting = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<WorkplaceId, CountDownLatch> latches = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<WorkplaceId, WorkplaceId> waitingForWhat = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<WorkplaceId, Long> taken = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<WorkplaceId, LinkedBlockingQueue<Long>> whoIsWaiting = new ConcurrentHashMap<>();
    private final Semaphore globalMutex = new Semaphore(1, true);
    private Integer howManyInside = 0;
    private final Semaphore s;

    private class NewWorkplace extends Workplace {
        WorkplaceId w;

        protected NewWorkplace(WorkplaceId id) {
            super(id);
        }

        protected NewWorkplace(WorkplaceId id, WorkplaceId w) {
            super(id);
            this.w = w;
        }

        @Override
        public void use() {
            try {
                if (latches.get(getId()) != null) {
                    latches.get(getId()).countDown();
                    latches.get(getId()).await();
                    latches.remove(getId());
                } else if (w != null) {
                    globalMutex.acquire();
                    if (whoIsWaiting.get(w).size() != 0) {
                        Long a = whoIsWaiting.get(w).poll();
                        waiting.get(a).release();
                        taken.put(w, a);
                    } else taken.remove(w);
                    globalMutex.release();
                }
                workplaces.get(getId()).use();
            } catch (InterruptedException e) {
                throw new RuntimeException("panic: unexpected thread interruption");
            }
        }
    }

    public WorkshopImplementation(Collection<Workplace> workplaces) {
        for (Workplace i : workplaces) {
            this.workplaces.put(i.getId(), i);
            this.whoIsWaiting.put(i.getId(), new LinkedBlockingQueue<>());
        }
        s = new Semaphore(2 * workplaces.size(), true);
    }

    @Override
    public Workplace enter(WorkplaceId wid) {
        try {
            threads.put(Thread.currentThread().getId(), wid);
            s.acquire();
            globalMutex.acquire();
            waiting.put(Thread.currentThread().getId(), new Semaphore(0, true));
            howManyInside++;
            if (taken.get(wid) == null) {
                taken.put(wid, Thread.currentThread().getId());
                globalMutex.release();
                return new NewWorkplace(wid);
            }
            whoIsWaiting.get(wid).add(Thread.currentThread().getId());
            globalMutex.release();
            waiting.get(Thread.currentThread().getId()).acquire();
            taken.put(wid, Thread.currentThread().getId());
            return new NewWorkplace(wid);
        } catch (InterruptedException e) {
            throw new RuntimeException("panic: unexpected thread interruption");
        }
    }

    @Override
    public Workplace switchTo(WorkplaceId wid) {
        try {
            WorkplaceId w = threads.get(Thread.currentThread().getId());
            threads.put(Thread.currentThread().getId(), wid);
            globalMutex.acquire();
            if (w == wid) {
                taken.put(wid, Thread.currentThread().getId());
                globalMutex.release();
                return new NewWorkplace(wid);
            }
            if (taken.get(wid) == null) {
                taken.put(wid, Thread.currentThread().getId());
                globalMutex.release();
                return new NewWorkplace(wid, w);
            }
            waitingForWhat.put(w, wid);
            whoIsWaiting.get(wid).add(Thread.currentThread().getId());
            if (latches.get(wid) == null) {
                Queue<WorkplaceId> q = new PriorityQueue<>();
                q.add(wid);
                WorkplaceId a = null;
                Queue<WorkplaceId> potentialCycle = new PriorityQueue<>();
                while (!q.isEmpty()) {
                    a = q.poll();
                    potentialCycle.add(a);
                    if (a == w) break;
                    if (waitingForWhat.get(a) == null) break;
                    q.add(waitingForWhat.get(a));
                }
                if (a == w) {
                    int cycleSize = potentialCycle.size();
                    CountDownLatch latch = new CountDownLatch(cycleSize);
                    for (int i = 0; i < cycleSize; i++) {
                        WorkplaceId hel = potentialCycle.poll();
                        latches.put(hel, latch);
                        whoIsWaiting.get(waitingForWhat.get(hel)).remove(taken.get(hel));
                        if (hel != w) {
                            waiting.get(taken.get(hel)).release();
                            waitingForWhat.remove(taken.get(hel));
                        }
                    }
                    waitingForWhat.remove(w);
                    taken.put(wid, Thread.currentThread().getId());
                    globalMutex.release();
                    return new NewWorkplace(wid, w);
                }
            }
            globalMutex.release();
            waiting.get(Thread.currentThread().getId()).acquire();
            globalMutex.acquire();
            waitingForWhat.remove(w);
            taken.put(wid, Thread.currentThread().getId());
            globalMutex.release();
            return new NewWorkplace(wid, w);
        } catch (InterruptedException e) {
            throw new RuntimeException("panic: unexpected thread interruption");
        }
    }

    @Override
    public void leave() {
        try {
            WorkplaceId w = threads.get(Thread.currentThread().getId());
            globalMutex.acquire();
            if (whoIsWaiting.get(w).size() != 0) {
                Long a = whoIsWaiting.get(w).poll();
                waiting.get(a).release();
                taken.put(w, a);
            } else taken.remove(w);
            waiting.remove(Thread.currentThread().getId());
            globalMutex.release();
            threads.remove(Thread.currentThread().getId());
            howManyInside--;
            if (howManyInside == 0) s.release(2 * workplaces.size() - s.availablePermits());
        } catch (InterruptedException e) {
            throw new RuntimeException("panic: unexpected thread interruption");
        }
    }
}
