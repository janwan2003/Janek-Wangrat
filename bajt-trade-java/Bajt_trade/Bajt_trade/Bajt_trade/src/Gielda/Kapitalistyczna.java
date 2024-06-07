package Gielda;

import Agent.Robotnik;
import Agent.Spekulant;

public class Kapitalistyczna extends Gielda {


    public Kapitalistyczna(int x, double[] c) {
        super(x, c);
    }

    @Override
    public void przeprowadzDzien(){
        for(int i = 0; i < 4; i++){

            sumaCen[i] = 0;
            iloscSprzedanych[i] = 0;
        }
        dzien++;
        for(Spekulant s : spekulanci){
            s.wystawOferty(this);
        }
        for(Robotnik r : robotnicy){
            r.przezyjDzien();
        }
        for(int i = 0; i < 4; i++){
            ceny.get(i).add(sumaCen[i] / iloscSprzedanych[i]);
            ilosc.get(i).add((int) iloscSprzedanych[i]);
            ofertyKupna.get(i).clear();
            ofertySprzedazy.get(i).clear();
        }

    }
}
