package StrategiaKupowania;

import Agent.Robotnik;
import Gielda.Gielda;

public class Zmechanizowany extends StrategiaKupowania{
    public Zmechanizowany(Robotnik r, Gielda g, int parametr) {
        super(r, g);
        this.parametr = parametr;
    }

    private final int parametr;
    @Override
    public void kup() {
        kupJedzenie();
        kupUbrania();
        kupNarzedzia(parametr);
    }
}
