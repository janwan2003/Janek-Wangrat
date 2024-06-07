% Author: Jan Wangrat
:- ensure_loaded(library(lists)).

:- op(700, xfx, <>). % operator of inequality

% init_state(+Program, +N, -InitialState)
% Program - representation of the program as a Prolog term
% N - number of processes
% InitialState - initial state of the system
% This predicate initializes the state of the system with all variables and arrays set to 0,
% and all program counters set to the beginning of the program (0).

init_state(program(Vars, Arrays, _Instr), N, state(InitVars, InitArrays, InitPC)) :-
    init_vars(Vars, InitVars),
    init_arrays(Arrays, N, InitArrays),
    init_pc(N, InitPC).

% init_pc(+N, -InitPC)
% N - number of processes
% InitPC - list of process counters initialized to zero

init_pc(0, []).
init_pc(N, [0|T]) :-
    N > 0,
    N1 is N - 1,
    init_pc(N1, T).

% init_vars(+Vars, -InitVars)
% Vars - list of variable names
% InitVars - list of variables initialized to zero

init_vars([], []).
init_vars([Var|Vars], [var(Var, 0)|InitVars]) :-
    init_vars(Vars, InitVars).

% init_arrays(+Arrays, +N, -InitArrays)
% Arrays - list of array names
% N - number of processes
% InitArrays - list of arrays initialized to zero

init_arrays([], _, []).
init_arrays([V|T1], N, [array(V, Arr)|T2]) :-
    init_array(N, Arr),
    init_arrays(T1, N, T2).

% init_array(+Size, -List)
% Size - size of the array
% List - array initialized with zeros

init_array(0, []).
init_array(N, [0|T]) :-
    N > 0,
    N1 is N - 1,
    init_array(N1, T).

% eval(+Expr, +State, +Pid, -Value)
% Expr - expression to evaluate
% State - state of the system
% Pid - process identifier
% Value - result of evaluation

eval(Expr, State, Pid, Value) :-
    eval_expr(Expr, State, Pid, Value).

eval_expr(pid, _, Pid, Pid).

eval_expr(Var, state(Vars, _, _), _, Value) :-
    atom(Var),
    member(var(Var, Value), Vars).

eval_expr(array(Arr, IndexExpr), state(Vars, Arrays, PCs), Pid, Value) :-
    eval_expr(IndexExpr, state(Vars, Arrays, PCs), Pid, Index),
    member(array(Arr, Values), Arrays),
    nth0(Index, Values, Value).

eval_expr(Expr, _, _, Value) :-
    number(Expr),
    Value is Expr.

eval_expr(Expr, State, Pid, Value) :-
    Expr =.. [Op, L, R],
    (is_arithmetic_operator(Op) ->
        eval_expr(L, State, Pid, LValue),
        eval_expr(R, State, Pid, RValue),
        compute(Op, LValue, RValue, Value)
    ; is_relational_operator(Op) ->
        eval_expr(L, State, Pid, LValue),
        eval_expr(R, State, Pid, RValue),
        compare(Op, LValue, RValue, Value)
    ).

% Check if operator is arithmetic
is_arithmetic_operator(+).
is_arithmetic_operator(-).
is_arithmetic_operator(*).
is_arithmetic_operator(/).

% Check if operator is relational
is_relational_operator(<).
is_relational_operator(>).
is_relational_operator(=).
is_relational_operator(<>).

% compute(+Op, +LValue, +RValue, -Value)
% Computes the result of arithmetic operations

compute(+, L, R, Value) :- Value is L + R.
compute(-, L, R, Value) :- Value is L - R.
compute(*, L, R, Value) :- Value is L * R.
compute(/, L, R, Value) :- Value is L // R.

% compare(+Op, +LValue, +RValue, -Value)
% Computes the result of comparison operations

compare(>, L, R, true) :- L > R.
compare(>, L, R, false) :- L =< R.
compare(<, L, R, true) :- L < R.
compare(<, L, R, false) :- L >= R.
compare(=, L, R, true) :- L =:= R.
compare(=, L, R, false) :- L =\= R.
compare(<>, L, R, true) :- L =\= R.
compare(<>, L, R, false) :- L =:= R.

% step(+Program, +State, +Pid, -NewState)
% Program - representation of the program as a Prolog term
% State - current state of the system
% Pid - process identifier
% NewState - new state of the system after one step

step(program(_, _, Instr), state(Vars, Arrays, PCs), Pid, state(NewVars, NewArrays, NewPCs)) :-
    nth0(Pid, PCs, PC), % Get the program counter for the current process
    nth0(PC, Instr, Instruction), % Get the current instruction
    execute(Instruction, state(Vars, Arrays, PCs), Pid, state(TempVars, TempArrays, TempPCs)),
    update_pc(Instruction, PC, TempPCs, NewPCs, Pid), % Update the program counter
    NewVars = TempVars,
    NewArrays = TempArrays.

% execute(+Instruction, +State, +Pid, -NewState)
% Instruction - instruction to execute
% State - current state of the system
% Pid - process identifier
% NewState - new state of the system after executing the instruction

execute(assign(Var, Expr), state(Vars, Arrays, PCs), Pid, state(NewVars, NewArrays, PCs)) :-
    eval(Expr, state(Vars, Arrays, PCs), Pid, Value),
    (   atomic(Var) -> 
        select(var(Var, _), Vars, var(Var, Value), NewVars),
        NewArrays = Arrays
    ;   Var = array(Arr, IndexExpr) ->
        eval(IndexExpr, state(Vars, Arrays, PCs), Pid, Index),
        select(array(Arr, OldArray), Arrays, array(Arr, NewArray), TempArrays),
        update_array(OldArray, Index, Value, NewArray),
        NewVars = Vars,
        NewArrays = TempArrays
    ).

execute(condGoto(Cond, Target), state(Vars, Arrays, PCs), Pid, state(Vars, Arrays, NewPCs)) :-
    eval(Cond, state(Vars, Arrays, PCs), Pid, true),
    Target1 is Target - 1,
    replace_nth0(PCs, Pid, Target1, NewPCs).
execute(condGoto(Cond, _), state(Vars, Arrays, PCs), Pid, state(Vars, Arrays, NewPCs)) :-
    eval(Cond, state(Vars, Arrays, PCs), Pid, false),
    nth0(Pid, PCs, PC),
    NextPC is PC + 1,
    replace_nth0(PCs, Pid, NextPC, NewPCs).
execute(goto(Target), state(Vars, Arrays, PCs), Pid, state(Vars, Arrays, NewPCs)) :-
    Target1 is Target - 1,
    replace_nth0(PCs, Pid, Target1, NewPCs).
execute(sekcja, state(Vars, Arrays, PCs), Pid, state(Vars, Arrays, NewPCs)) :-
    nth0(Pid, PCs, PC),
    NextPC is PC + 1,
    replace_nth0(PCs, Pid, NextPC, NewPCs).

% update_pc(+Instruction, +PC, +TempPCs, -NewPCs, +Pid)
% Instruction - instruction that was executed
% PC - current program counter
% TempPCs - program counters after executing the instruction
% NewPCs - updated program counters
% Pid - process identifier

update_pc(assign(_, _), PC, PCs, NewPCs, Pid) :-
    NextPC is PC + 1,
    replace_nth0(PCs, Pid, NextPC, NewPCs).

update_pc(condGoto(_, _), _, PCs, PCs, _). % PC already updated in execute/4 for condGoto

update_pc(goto(_), _, PCs, PCs, _). % PC already updated in execute/4 for goto

update_pc(sekcja, _, PCs, PCs, _).

% replace_nth0(+List, +Index, +Elem, -NewList)
% List - input list
% Index - index at which the element should be replaced
% Elem - new element
% NewList - resulting list

replace_nth0(List, Index, Elem, NewList) :-
    nth0(Index, List, _, Rest),
    nth0(Index, NewList, Elem, Rest).

% update_array(+OldArray, +Index, +Value, -NewArray)
% OldArray - old array
% Index - index to update
% Value - new value
% NewArray - new array after update

update_array([_|T], 0, Value, [Value|T]).
update_array([H|T], Index, Value, [H|NewT]) :-
    Index > 0,
    Index1 is Index - 1,
    update_array(T, Index1, Value, NewT).

% in_section(+ProcessCounters, +ProgramContent, -ProcessListInSection)
% ProcessCounters - list of process counters
% ProgramContent - list of instructions
% ProcessListInSection - list of processes in the critical section

in_section(Ps, Stmts, In) :- in_section(Ps, Stmts, 0, In).

% in_section(+ProcessCounters, +ProgramContent, +Process, -ProcessListInSection)
% ProcessCounters - list of process counters
% ProgramContent - list of instructions
% Process - current process index
% ProcessListInSection - list of processes in the critical section

in_section([], _, _, []).
in_section([H|T], Stmts, I, Res) :-
	H1 is H,
    (nth0(H1, Stmts, sekcja) ->
	    Res = [I|L];
	    Res = L
	),
	I1 is I + 1,
	in_section(T, Stmts, I1, L).

% collision(+Program, +State, -ProcessList)
% Program - representation of the program as a Prolog term
% State - current state of the system
% ProcessList - list of processes in the critical section

collision(program(_, _, Stmts), state(_, _, Ps), L) :-
	in_section(Ps, Stmts, L),
	length(L, In),
	In > 1.

% collision(+Program, +State)
% Program - representation of the program as a Prolog term
% State - current state of the system

collision(Program, State) :- collision(Program, State, _).

% run(+Program, +N, -FinalState)
% Program - representation of the program as a Prolog term
% N - number of processes
% FinalState - final state of the system

run(Program, N, FinalState) :-
    init_state(Program, N, State),
    (dfs(Program, State, N, [], [], [], FinalState) ->
        true
    ;
        write('Program jest poprawny (bezpieczny).\n'),
        FinalState = gites).

% Mam pełną świadomość, że nie działa mi odwiedzanie stanów,
% nie udało mi się jednak wymęczyć tego tak żeby działało

% dfs(+Program, +State, +N, +Visited, +Stack, -FinalState)
% Program - representation of the program as a Prolog term
% State - current state of the system
% N - number of processes
% Visited - list of visited states
% Stack - stack of executed steps
% FinalState - final state of the system after DFS traversal

dfs(_, State, _, Visited, Visited, _, noerror) :-
    member(State, Visited).

dfs(Program, State, _, _, _, Stack, error) :-
    collision(Program, State, L),
    write('Program jest niepoprawny.\n'),
    write('Niepoprawny przeplot:\n'),
    print_stack_trace(Stack),
    write('Procesy w sekcji: '),
    print_list(L),
    write('.\n').

dfs(Program, State, N, Visited, Visited2, Stack, FinalState) :-
    \+ collision(Program, State, _),
    \+ member(State, Visited),
    dfs_all_steps(Program, State, N, 0, [State|Visited], Visited2, Stack, FinalState).

% dfs_all_steps(+Program, +State, +N, +Pid, +Visited, +Stack, -FinalState)
% Program - representation of the program as a Prolog term
% State - current state of the system
% N - number of processes
% Pid - process identifier
% Visited - list of visited states
% Stack - stack of executed steps
% FinalState - final state of the system after DFS traversal of all steps

dfs_all_steps(Program, State, N, Pid, Visited, Visited2, Stack, FinalState) :-
    Pid < N,
    step(Program, State, Pid, NewState),
    find_current(State, Pid, Cur),
    append(Stack, [step(Pid, Cur)], TempStack),
    (dfs(Program, NewState, N, Visited, Visited2, TempStack, FinalState) ->
        true
    ;
        Pid1 is Pid + 1,
        dfs_all_steps(Program, State, N, Pid1, Visited, Visited2, Stack, FinalState)
    ).

dfs_all_steps(_, _, N, N, Visited, Visited, noerror).

% find_current(+State, +Pid, -Cur)
% State - current state of the system
% Pid - current process ID
% Cur - current instruction pointer

find_current(state(_, _, Ps), Pid, Cur) :-
    nth0(Pid, Ps, Cur).

% Helper to print the stack trace
print_stack_trace([]).
print_stack_trace([step(Pid, PC)|Rest]) :-
    write('Proces '), write(Pid), write(': '), 
    PC1 is PC + 1, write(PC1), write('\n'),
    print_stack_trace(Rest).

% Helper to print a list of items separated by commas
print_list([Item]) :-
    write(Item).
print_list([Item|Rest]) :-
    write(Item), write(', '),
    print_list(Rest).

% Reading the program from a file
read_program(FileName, program(Vars, Arrays, Instr)) :-
    see(FileName),
    read(variables(Vars)),
    read(arrays(Arrays)),
    read(program(Instr)),
    seen.

% verify(+N, +FileName)
% N - number of processes
% FileName - name of the file containing the program

verify(N, FileName) :-
    set_prolog_flag(fileerrors, off),    
    (integer(N), N > 0 ->
        (see(FileName) ->
            read_program(FileName, Program),
            run(Program, N, _)
        ;
            write('Error: brak pliku o nazwie - '), write(FileName), write('\n'))
    ;
        write('Error: parametr '), write(N), write(' powinien byc liczba > 0\n')).

% :- verify(1, 'tests/test1.txt').
% :- verify(3, 'tests/test2.txt').
% :- verify(3, 'tests/test3.txt').
% :- verify(2, 'tests/test4.txt').
% :- verify(3, 'tests/test5.txt').
% :- verify(2, 'tests/test6.txt').