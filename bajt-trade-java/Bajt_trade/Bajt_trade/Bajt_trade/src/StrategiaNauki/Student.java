package StrategiaNauki;

import Agent.Robotnik;
import Gielda.Gielda;

public class Student implements StrategiaNauki{
    private final int okres;
    private final int zapas;
    public Student(int okres, int zapas){
        this.okres = okres;
        this.zapas = zapas;
    }
    @Override
    public boolean czySieUczyc(Robotnik r, Gielda g){
        return 100 * zapas * g.liczSrednieCeny(g.getCeny().get(1), okres) <= r.getDiamenty();
    }
}
