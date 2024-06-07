package StrategiaProdukcji;

import Agent.Robotnik;
import Gielda.Gielda;

public class Sredniak extends StrategiaProdukcji {
    private int parametr;

    public Sredniak(Gielda g, Robotnik r) {
        super(g, r);
    }

    @Override
    public int idProdukowanego() {
        int najId = 0;
        double cena = 0;
        for(int i = 0; i < 4; i++){
            int k = Math.min(g.getDzien(), parametr);
            for(int j = 0; j < k; j++){
                if(g.getCeny().get(i).get(g.getCeny().get(i).size() - 1 - j) > cena){
                    najId = i;
                    cena = g.getCeny().get(i).get(g.getCeny().get(i).size() - 1 - j);
                }
            }
        }
        return najId;
    }
}
