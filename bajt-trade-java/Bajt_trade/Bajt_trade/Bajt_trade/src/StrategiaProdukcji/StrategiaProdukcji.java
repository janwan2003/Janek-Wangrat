package StrategiaProdukcji;

import Agent.Robotnik;
import Gielda.Gielda;

abstract public class StrategiaProdukcji {
    protected Gielda g;
    protected Robotnik r;

    public StrategiaProdukcji(Gielda g, Robotnik r) {
        this.g = g;
        this.r = r;
    }

    abstract public int idProdukowanego();
}
