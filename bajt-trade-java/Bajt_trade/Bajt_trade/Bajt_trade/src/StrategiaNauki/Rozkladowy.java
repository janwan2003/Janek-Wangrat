package StrategiaNauki;

import Agent.Robotnik;
import Gielda.Gielda;

import java.util.Random;

public class Rozkladowy implements StrategiaNauki{
    final Random rand = new Random();
    @Override
    public boolean czySieUczyc(Robotnik r, Gielda g){
        double d = rand.nextDouble();
        return !(d > (double) (1 / (g.getDzien() + 3)));
    }
}
