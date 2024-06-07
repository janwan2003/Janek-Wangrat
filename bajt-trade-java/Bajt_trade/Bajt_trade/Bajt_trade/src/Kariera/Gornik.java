package Kariera;

import Agent.Robotnik;
import Gielda.Gielda;

public class Gornik extends Kariera{
    public Gornik(int poziom, Robotnik r, Gielda g) {
        super(poziom, 4, r, g);
    }

    @Override
    public void produkujDiamenty(){
        double modyfikator = liczModyfikatorZBonusem();
        r.zmienDiamenty(modyfikator * r.dajProduktywnosc(4)/100);
    }
}
