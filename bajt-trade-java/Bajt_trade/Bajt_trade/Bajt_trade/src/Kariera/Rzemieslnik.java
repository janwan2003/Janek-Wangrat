package Kariera;

import Agent.Robotnik;
import Gielda.Gielda;
import Produkty.Ubrania;

import java.util.Collections;
import java.util.Vector;

public class Rzemieslnik extends Kariera{
    public Rzemieslnik(int poziom, Robotnik r, Gielda g) {
        super(poziom, 0, r, g);
    }

    @Override
    public void produkujUbrania(){
        int modyfikator = liczModyfikatorZBonusem();
        if(modyfikator == 0)return;
        int ile_wyprodukuje = modyfikator * r.dajProduktywnosc(0) / 100;
        Vector<Ubrania> wynik = new Vector<>();
        int k = Math.min(r.getProgramy().size(), ile_wyprodukuje);
        Collections.sort(r.getProgramy());
        for (int i = 0; i < k; i++) {
            wynik.add(new Ubrania(r.getProgramy().get(r.getProgramy().size() - 1).poziom));
            r.getProgramy().remove(r.getProgramy().size() - 1);
            ile_wyprodukuje--;
        }
        for (int i = 0; i < ile_wyprodukuje; i++) {
            wynik.add(new Ubrania(1));
        }
        int ilo = 1;
        for(int i = 1; i < wynik.size(); i++){
            if(wynik.get(i).poziom != wynik.get(i-1).poziom){
                r.sprzedaj(0, ilo, wynik.get(i-1).poziom);
                ilo = 1;
            }
            else ilo++;
        }
        r.sprzedaj(0, ilo, wynik.get(wynik.size()-1).poziom);
    }
}
