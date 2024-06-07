package StrategiaKupowania;

import Agent.Robotnik;
import Gielda.Gielda;

public class Gadzeciarz extends StrategiaKupowania{
    private final int parametr;

    public Gadzeciarz(Robotnik r, Gielda g, int parametr) {
        super(r, g);
        this.parametr = parametr;
    }


    @Override
    public void kup() {
        kupJedzenie();
        kupUbrania();
        kupNarzedzia(parametr);
        kupProgramy();
    }
}
