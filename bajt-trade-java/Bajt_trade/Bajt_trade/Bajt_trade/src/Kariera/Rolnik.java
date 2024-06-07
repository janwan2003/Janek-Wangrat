package Kariera;

import Agent.Robotnik;
import Gielda.Gielda;

public class Rolnik extends Kariera{
    public Rolnik(int poziom, Robotnik r, Gielda g) {
        super(poziom, 0, r, g);
    }

    @Override
    public void produkujJedzenie(){
        int modyfikator = liczModyfikatorZBonusem();
        r.sprzedaj(1, modyfikator * r.dajProduktywnosc(1), 0);
    }
}
