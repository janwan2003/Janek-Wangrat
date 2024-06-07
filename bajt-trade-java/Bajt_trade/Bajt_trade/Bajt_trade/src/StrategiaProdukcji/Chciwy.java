package StrategiaProdukcji;

import Agent.Robotnik;
import Gielda.Gielda;

public class Chciwy extends StrategiaProdukcji {

    public Chciwy(Gielda g, Robotnik r) {
        super(g, r);
    }

    @Override
    public int idProdukowanego() {
        int najId = 0;
        double zarobek = 0;
        for(int i =0; i <= 4; i++){
            int ileWyprodukuje;
            if(r.getKariera().getId() == i)ileWyprodukuje = r.getKariera().liczModyfikatorZBonusem();
            else ileWyprodukuje = r.getKariera().liczModyfikator();
            double ileZarobi;
            if(i != 4)
            ileZarobi = ileWyprodukuje * g.getCeny().get(i).get(g.getCeny().get(i).size() - 1);
            else{
                ileZarobi = ileWyprodukuje;
            }
            if(ileZarobi > zarobek) {
                najId = i;
                zarobek = ileZarobi;
            }
        }
        return najId;
    }
}
