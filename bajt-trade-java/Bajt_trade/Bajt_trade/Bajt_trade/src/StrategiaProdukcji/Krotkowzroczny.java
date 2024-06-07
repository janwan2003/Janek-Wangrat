package StrategiaProdukcji;

import Agent.Robotnik;
import Gielda.Gielda;

public class Krotkowzroczny extends StrategiaProdukcji {
    public Krotkowzroczny(Gielda g, Robotnik r) {
        super(g, r);
    }

    @Override
    public int idProdukowanego() {
        int id = 0;
        double max = 0;
        for(int i = 0 ; i < 4; i++){
            if(g.getCeny().get(i).get(g.getCeny().get(i).size() - 1) > max){
                max = g.getCeny().get(i).get(g.getCeny().get(i).size() - 1);
                id = i;
            }
        }
        return id;
    }
}
