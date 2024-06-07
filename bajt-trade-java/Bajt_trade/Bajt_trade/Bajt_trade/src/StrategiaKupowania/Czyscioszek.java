package StrategiaKupowania;

import Agent.Robotnik;
import Gielda.Gielda;

public class Czyscioszek extends StrategiaKupowania{

    public Czyscioszek(Robotnik r, Gielda g) {
        super(r, g);
    }

    @Override
    public void kup(){
        kupJedzenie();
        kupUbrania();
    }
}
