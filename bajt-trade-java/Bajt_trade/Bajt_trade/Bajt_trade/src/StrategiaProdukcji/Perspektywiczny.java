package StrategiaProdukcji;

import Agent.Robotnik;
import Gielda.Gielda;

public class Perspektywiczny extends StrategiaProdukcji {
    private final int parametr;

    public Perspektywiczny(Gielda g, Robotnik r, int parametr) {
        super(g, r);
        this.parametr = parametr;
    }


    @Override
    public int idProdukowanego() {
        int id = 0;
        double cena = Double.MIN_VALUE;
        int a = Math.min(g.getDzien() - 1, parametr);
        for(int i = 0; i < 4; i++){
            double k = g.getCeny().get(i).get(g.getDzien() - 1) - g.getCeny().get(i).get(g.getDzien() - 1 - a);
            if(k > cena){
                id = i;
                cena = k;
            }
        }
        return id;
    }
}
