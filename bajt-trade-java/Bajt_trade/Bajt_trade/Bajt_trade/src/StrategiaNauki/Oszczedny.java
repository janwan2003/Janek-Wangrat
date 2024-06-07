package StrategiaNauki;

import Agent.Robotnik;
import Gielda.Gielda;

public class Oszczedny implements StrategiaNauki {
    private final int w;
    public Oszczedny(int w){
        this.w = w;
    }
    @Override
    public boolean czySieUczyc(Robotnik r, Gielda g){
        return r.getDiamenty() > w;
    }
}
