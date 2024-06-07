package StrategiaKariery;

import Agent.Robotnik;
import Gielda.Gielda;

public class Rewolucjonista extends StrategiaKariery{
    public Rewolucjonista(Robotnik r, Gielda g) {
        super(r, g);
    }

    @Override
    public void ZmienSciezke() {
        if(g.getDzien() % 7 == 0){
            int k = r.getId() % 17;
            int sciezka = 0;
            int suma = 0;
            int pom = Math.min(g.getDzien() -1, k);
            for(int i = 0; i < 4; i++){
                int l = g.liczSumeOfert(g.getIlosc().get(i), pom);
                if(l > suma) {
                    sciezka = i;
                    suma = l;
                }
            }
            if(sciezka == r.getKariera().getId())r.getKariera().ucz_sie();
            else r.zmienSciezke(sciezka);
        }
    }
}
