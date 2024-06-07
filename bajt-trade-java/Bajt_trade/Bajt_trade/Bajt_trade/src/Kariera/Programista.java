package Kariera;

import Agent.Robotnik;
import Gielda.Gielda;

public class Programista extends Kariera{
    public Programista(int poziom, Robotnik r, Gielda g) {
        super(poziom, 3, r, g);
    }

    @Override
    public void produkujProgramy(){
        int modyfikator = liczModyfikatorZBonusem();
        r.sprzedaj(3, modyfikator * r.dajProduktywnosc(3), poziom);
    }
}
