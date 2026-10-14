# Jan Wangrat — Project Portfolio

I build LLM-powered products end to end: retrieval and agent pipelines, the FastAPI/Python
services behind them, the React frontends on top, and the infra they run on.

BSc Computer Science (2024) and BSc Mathematics (2025), University of Warsaw.
MSc Computer Science and Engineering at Politecnico di Milano (expected 2027). Alongside the
degree I run applied-AI projects with industry partners at
[Polimi Data Scientists](#polimi-data-scientists-20252026).

- **Email**: [jan@wangrat.com](mailto:jan@wangrat.com)
- **LinkedIn**: [jan-wangrat](https://www.linkedin.com/in/jan-wangrat-b2b651236/)
- **CV**: [Jan_Wangrat_CV.pdf](Jan_Wangrat_CV.pdf)
- **Location**: Milan / Warsaw

## Where I work

| | Role |
| --- | --- |
| **[Neural Alpha](https://neuralalpha.com)** | AI Software Developer — backend and data engineering |
| **[Polimi Data Scientists](https://www.polimidatascientists.it)** | Project manager (2026) — student association, AI projects with company partners |

### Neural Alpha

[Neural Alpha](https://neuralalpha.com) does sustainability and corporate-performance analytics for
investors and researchers. The premise is that an ESG score you can't audit is worthless: an analyst
brings their own methodology — or picks up an off-the-shelf framework like ISSB, CSRD or TNFD — asks
questions of a company's own filings, and gets back an answer with the source passage attached. Work
that took analyst-weeks lands in hours, over a universe most teams can't afford to cover manually.

Publicly stated scale: **1.8M** financial and sustainability disclosures across **200K** public and
private companies, **2.6M** metrics and KPIs, **17M** news stories for controversy screening, and
**250K** modelled financial and sustainability concepts. One published engagement assessed 1.4M
disclosures for 7,000 companies against the TNFD recommendations.

What I work with on the backend and data side:

- **Python services** — FastAPI serving the platform, **Celery** + **Redis** for assessment runs that
  outlive a request, **MongoDB** as the operational store (async and sync clients side by side)
- **Dagster** pipelines on both halves of the system: building the entity graph and the exportable data
  products, and running the online and offline AI work behind the app. Older **Kedro** pipelines are
  still upstream of some of it
- **A shared LangGraph agent library** consumed as a local dependency by both the API and the
  pipelines — grounded document Q&A, chat, and document processing over retrieved disclosures, against
  several model providers
- **Neo4j** for the entity graph; **AWS**, **Terraform** and **Helm/Kubernetes** for deployment;
  **Next.js** on the frontend; `uv` and `pnpm` across a Python + TypeScript monorepo

Neural Alpha's code and data belong to Neural Alpha — none of it is published here, and nothing above
goes past what the company states publicly plus the technologies involved.

---

## Research & theses

| Year | Project | What it is | Stack |
| --- | --- | --- | --- |
| 2025–26 | [**Intelligent Job Management**](https://github.com/janwan2003/intelligent-job-management) — MSc research project, supervised by Prof. Danilo Ardagna | A scheduler for shared GPU deep-learning clusters, modelled after Polimi's ANDREAS project. It profiles each job on each GPU type, places jobs to meet their deadlines, and stops, migrates and resumes running jobs from checkpoints when a better plan appears. Runs on Polimi's own A40 and P600 nodes; components coordinate through PostgreSQL `LISTEN`/`NOTIFY`, with no message broker. | Python, FastAPI, PostgreSQL, Docker, React |
| 2025 | [**Unbalanced Optimal Transport for NMR mixture analysis**](bachelor-thesis-MirrorDescent) — BSc Mathematics thesis | A scalable mirror-descent method for estimating spectral components of NMR mixtures via unbalanced optimal transport. Experiment code: [`janwan2003/wasserstein`](https://github.com/janwan2003/wasserstein) | Python, optimal transport |
| 2024 | [**DucklingLS**](bachelor-thesis-DucklingLS) — BSc Computer Science thesis | Language-server support for a new programming language: LSP design, incremental analysis, editor integration | TypeScript, LSP |

---

## Polimi Data Scientists (2025–2026)

[Polimi Data Scientists](https://www.polimidatascientists.it) (PMDS) is a student-led association at
Politecnico di Milano that runs data-science projects with company partners. Both of my projects
there ended in a paper.

### ChatDDT — project manager, with [AIDEA](https://aidea.team) (Feb–Jun 2026)

AIDEA wanted delivery notes (*documenti di trasporto*, DDT) and invoices from many different
suppliers turned into structured data, without maintaining a template per supplier. I managed the
project for a three-person student team: planned the work and kept it in step with AIDEA, set up the
repository, CI and issue workflow, and wrote the benchmark and the final report.

- **15 extraction pipelines benchmarked head to head** — native vision–language models, local and
  cloud OCR, layout parsers, table detection, hosted extractors — with the model, prompt and schema
  held fixed, so only the document front-end varies.
- **The answer for AIDEA:** the leading pipelines reach about 93% field accuracy at about a cent per
  document, and sending the raw PDF straight to the model is statistically tied at under half the
  cost. The remaining errors are mostly omissions, not hallucinations.
- **Audit mode** — a FastAPI + React review app that shows, for every extracted field, where on the
  page it came from.

*Gemini, LlamaParse, Surya OCR, Google Document AI, Python, FastAPI, React*

### Market Regime Detector — with [BlackSwan Quants](https://blackswanquants.com) (Nov 2025–Mar 2026)

A nine-person team building a detector that tells calm, transitional and crisis regimes of the
S&P 500 apart, as the input layer for an intraday momentum strategy. The final model is a Gaussian
hidden Markov model (HMM). I built the dynamic-time-warping (DTW) hierarchical-clustering detector
it is benchmarked against, and set up the repository's tooling.

- **Result:** the HMM's high-volatility regime runs at about 2.3× the realised volatility of the calm
  one, while the clustering baseline labels real drawdowns as low-risk periods.

*Python, hmmlearn, DTW clustering, pandas* · [repo](https://github.com/blackswan-quants/marketregime_hmm)

### Publications

PDFs are in [`papers/`](papers).

1. **J. Wangrat**, V. Caracuianu, S. Rajeev, F. Romeo. *Generalist Structured Extraction of Italian
   Transport Documents and Invoices: A Controlled Benchmark of OCR, Layout-Parsing, and
   Vision–Language Pipelines.* Technical report, Politecnico di Milano and AIDEA, 2026.
   First and corresponding author. [PDF](papers/Wangrat_2026_Structured_Extraction_Italian_Transport_Documents.pdf)
2. L. Pirozzi, G. Talà, S. Zani, M. Misimović, M. Puppo, E. Renzoni, V. Sironi, **J. Wangrat**,
   M. Zappa. *A Comparative Study of Gaussian HMMs and DTW Hierarchical Clustering for S&P 500
   Volatility Regime Identification.* SSRN preprint, April 2026.
   [doi:10.2139/ssrn.6627619](https://doi.org/10.2139/ssrn.6627619)
3. **J. Wangrat**. *Scheduling, Planning and Execution for Lifelong Multi-Agent Pickup and Delivery
   in the League of Robot Runners 2026.* Draft, 2026. [PDF](papers/Wangrat_2026_LoRR_Lifelong_MAPD_draft.pdf)
4. M. Orżanowski, **J. Wangrat**. *NMR Spectral Component Estimation using Unbalanced Optimal Transport
   via Mirror Descent Algorithm.* BSc thesis in Mathematics, University of Warsaw, 2025.
   [PDF](bachelor-thesis-MirrorDescent/NMR_Spectral_Component_Estimation_using_Unbalanced_Optimal_Transport_via_Mirror_Descent_Algorithm.pdf)
5. S. Dziuda, J. Kołaczyński, J. Szot, **J. Wangrat**. *Language Server for Duckling Programming Language.*
   BSc thesis in Computer Science, University of Warsaw, 2024.
   [PDF](bachelor-thesis-DucklingLS/Language%20server%20for%20Duckling%20programming%20language.pdf)

---

## Recent work (2025–2026)

Larger projects live in their own repositories. Ordered by how much work went into each.

| Year | Project | What it is | Stack | Links |
| --- | --- | --- | --- | --- |
| 2026 | **TaxCompass** | AI tax co-pilot for foreign founders opening a business in Italy, live in production. Every answer links to the law it rests on (Normattiva, Agenzia delle Entrate, INPS): multi-agent RAG with inline citations, a deterministic tax engine comparing Italy, Poland and Romania, user accounts with saved chats, and a prerendered SEO site. | LangGraph, FastAPI, pgvector, Azure OpenAI, React, Terraform | [taxcompass.it](https://taxcompass.it) · repo private |
| 2026 | **Robot fleet planner — League of Robot Runners** | Task assignment and path planning for up to 10,000 warehouse robots, with one second of compute per round and tasks revealed online. Optimal Hungarian matching on smaller rounds, guide paths from traffic-flow optimisation (Frank–Wolfe), a tick-by-tick executor, and per-map tactics such as zoning and traffic lights at bottlenecks. Finished **2nd on all three tracks**. A GitHub Actions queue kept submitting entries unattended for three months. | C++, OpenMP, Python, GitHub Actions | repo private |
| 2026 | **RewAIre** | Turns a company's workflow evidence — interview transcripts, emails, policies, system logs — into an AI-transformation blueprint through three LLM layers (Observe → Judge → Recommend), explorable as an interactive workflow graph. Three-student Polimi capstone built with [ai71](https://ai71.ai); I own the Judge layer, the app shell and the frontend. | FastAPI, MongoDB/GridFS, Pydantic, Claude, React Flow | repo private |
| 2026 | **WeGoWhen** | Group trip date finder: everyone marks the days they're free, and a heat map plus ranked date ranges show when the whole group can go. No accounts — the shared link is the only credential. Prerendered, indexable pages in eight languages. In use by strangers since September 2026: 30+ trips and 100+ participants by October. | React, TypeScript, Cloudflare Pages Functions, D1 | [wegowhen.com](https://wegowhen.com) · repo private |
| 2025–26 | **Embedding Model Selection Platform** | Benchmarks embedding models on your own documents, so you choose by retrieval quality on your data rather than a public leaderboard. Team project, serverless on Azure. | FastAPI, React, Azure Functions, Cosmos DB, Azure OpenAI | [repo](https://github.com/EmbedMatch/embedding-project-cloud) |
| 2026 | **prompt-debt** | Claude Code skill that reads your own session transcripts, measures how many prompts each task cost you, and turns the follow-ups you keep typing into skills and rules. Recurring mistakes are classified by three blind LLM readers, with their agreement measured. Standard library only, no network calls, 115 tests. | Python, Claude Code | repo private |
| 2026 | **Exploding Kittens on your LAN** | Self-hosted card game for 2–10 players: authoritative Socket.IO server owns all hidden state, shared TypeScript types make a protocol break a compile error, 108 tests over the rules engine (Nope-on-Nope stacking included). | TypeScript, Socket.IO, React | [repo](https://github.com/janwan2003/exploding-kittens-lan) |
| 2026 | **PokerChips** | Poker chips on phones: one Android phone hosts the game, everyone else scans a QR code and plays in the browser over local Wi-Fi. Every action is journaled on the host, so a crashed app or a dead phone resumes exactly where it stopped. | Kotlin, Android | [repo](https://github.com/janwan2003/pokerchips) |
| 2026 | **Sandbox Party** | Jackbox-style party game with an LLM game master: authoritative Colyseus server drives the phase machine and the model, a Godot 4.6 screen renders, and everyone plays from their phone over the LAN. | Node.js, Colyseus, Godot/GDScript, Gemini | [repo](https://github.com/janwan2003/sandbox-party) |
| 2026 | **LLM Calculation Benchmark** | 500 calculation problems with tool-verified ground truth (SymPy / `Fraction`), plus a pipeline that pulls real olympiad problems from published PDFs and an async evaluation notebook. Gemini 2.5 Flash scores 83.4% — and fails on applied multi-step problems, not on the ones labelled hard. | Python, SymPy, LangChain, MongoDB/JSON | [repo](https://github.com/janwan2003/llm-calculation-benchmark) |
| 2024 | **Goldman Sachs hackathon** | Calendar-integrated scheduling service built in 24 hours; 3rd place. | TypeScript, Python | [frontend](https://github.com/goldman-hackathon/goldman-front) · [backend](https://github.com/goldman-hackathon/goldman-back) |

---

## Competitions

| Year | Result | |
| --- | --- | --- |
| 2026 | **2nd of 68 teams** — [League of Robot Runners](https://www.leagueofrobotrunners.org) | International multi-agent path-finding competition sponsored by Amazon Robotics, co-hosted with AAMAS 2026. Entered as *Trzy Kwaterki*; 2nd on all three tracks (Combined, Execution, Scheduling), combined score 7.034. Planner described above. |
| 2026 | **3rd place** — [Hackarena 3.0](https://hackarena.pl), Warsaw | |
| 2025 | **21st in the final** — Midnight Code Competition, Serbia | |
| 2025 | **1st place** — Hackarena 2.5, Warsaw | |
| 2024 | **1st place** — Hackarena 2.0, Warsaw | |
| 2024 | **3rd place** — AWS DeepRacer Challenge, University of Warsaw | |
| 2024 | **3rd place** — Goldman Sachs Hackathon, Warsaw | Software engineering track. Code linked above. |
| — | **Finalist** — Polish Olympiad in Mathematics | One of three **Andrzej Mąkowski Awards** for the best solution to a problem. |
| — | **Final** without the **Finalist** title — Polish Olympiad in Informatics | |

Also selected for **First Ascent 2025**, Bending Spoons' invitation-only retreat for computer
science students.

---

## University of Warsaw coursework (2021–2024)

Kept for the record — course assignments from the CS and Mathematics degrees, not current
work. Stars are my own take on how interesting each one is.

<details>
<summary>Show 15 projects</summary>

| Year | Project | What it is | Course / stack | |
| --- | --- | --- | --- | --- |
| 2023/24 | [python-buses-project](python-buses-project) | Warsaw bus network simulation and analysis | Python, Dagster | ★★★★★ |
| 2023/24 | [interpreter-janeklang](interpreter-janeklang) | Interpreter for a custom language, with editor tooling | Haskell, Programming Languages and Tools | ★★★★★ |
| 2023/24 | [deep-neural-networks](deep-neural-networks) | Deep Neural Networks course assignments | Python, PyTorch | ★★★★★ |
| 2022/23 | [executor](executor) | Concurrent task execution and management utility | C, Parallel Programming | ★★★★☆ |
| 2022/23 | [internet-radio](internet-radio) | Internet radio sender/receiver with retransmission | C++, Java, Computer Networks | ★★★★☆ |
| 2022/23 | [nice-graph-drawing-cpp](nice-graph-drawing-cpp) | Graph layout and drawing | C++, LaTeX, Software Engineering | ★★★★☆ |
| 2022/23 | [c-program-compiler-web-aplication](c-program-compiler-web-aplication) | Web front end for compiling and running C programs | Python, Django, WWW Applications | ★★★☆☆ |
| 2021/22 | [java-matrices-operations](java-matrices-operations) | Matrix operations library | Java, Object-Oriented Programming | ★★★☆☆ |
| 2022/23 | [java-workers-factory](java-workers-factory) | Worker/task assignment simulation | Java, Parallel Programming | ★★★☆☆ |
| 2021/22 | [bajt-trade-java](bajt-trade-java) | Multi-agent trading simulation | Java, Object-Oriented Programming | ★★☆☆☆ |
| 2022/23 | [distributed-stack-machine](distributed-stack-machine) | Stack machine across distributed nodes | Assembly, Operating Systems | ★★☆☆☆ |
| 2022/23 | [inverting-permutation](inverting-permutation) | In-place permutation inversion | Assembly, Operating Systems | ★★☆☆☆ |
| 2023/24 | [prolog-program-verifier](prolog-program-verifier) | Verifier for a small imperative language | Prolog, Programming Languages and Tools | ★★☆☆☆ |
| 2021/22 | [ski-jumping-website](ski-jumping-website) | Ski jumping contest management site | PHP, PostgreSQL, Databases | ★☆☆☆☆ |
| 2023/24 | [haskell-graph-and-set](haskell-graph-and-set) | Functional set and graph manipulation | Haskell, Programming Languages and Tools | ★☆☆☆☆ |

</details>

---

## Stack

**Languages** — Python, TypeScript, Java, C/C++, Haskell, Kotlin, SQL, Assembly

**AI / data** — LangGraph, LangChain, RAG and hybrid retrieval, pgvector, Qdrant, Neo4j,
PyTorch, Azure OpenAI, Anthropic API, Gemini, document extraction (LlamaParse, Surya OCR,
Document AI), hidden Markov models

**Backend** — FastAPI, SQLAlchemy, Pydantic, MongoDB, PostgreSQL, MySQL, Django

**Frontend** — React, Vite, Tailwind, shadcn/ui, TanStack Query

**Infra** — Docker, Terraform, AWS, Azure, GitHub Actions
