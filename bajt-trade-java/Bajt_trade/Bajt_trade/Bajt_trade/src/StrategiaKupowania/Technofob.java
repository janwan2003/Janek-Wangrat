package StrategiaKupowania;

import Agent.Robotnik;
import Gielda.Gielda;

public class Technofob extends StrategiaKupowania{
    public Technofob(Robotnik r, Gielda g) {
        super(r, g);
    }

    @Override
    public void kup() {
        kupJedzenie();
    }
}
