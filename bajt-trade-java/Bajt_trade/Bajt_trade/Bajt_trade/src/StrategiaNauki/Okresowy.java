package StrategiaNauki;

import Agent.Robotnik;
import Gielda.Gielda;

public class Okresowy implements StrategiaNauki{
    final int okres;
    public Okresowy(int a){
        this.okres = a;
    }
    @Override
    public boolean czySieUczyc(Robotnik r, Gielda g){
        return g.getDzien() % okres == 0;
    }
}
