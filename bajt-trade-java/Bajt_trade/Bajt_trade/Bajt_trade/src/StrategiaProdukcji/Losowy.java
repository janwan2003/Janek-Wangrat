package StrategiaProdukcji;

import Agent.Robotnik;
import Gielda.Gielda;

import java.util.Random;

public class Losowy extends StrategiaProdukcji {
    final Random r = new Random();

    public Losowy(Gielda g, Robotnik r) {
        super(g, r);
    }

    @Override
    public int idProdukowanego() {
        return r.nextInt(5);
    }
}
