package StrategiaKariery;

import Agent.Robotnik;
import Gielda.Gielda;

abstract public class StrategiaKariery {
    final Robotnik r;
    final Gielda g;
    abstract public void ZmienSciezke();

    public StrategiaKariery(Robotnik r, Gielda g) {
        this.r = r;
        this.g = g;
    }
}
