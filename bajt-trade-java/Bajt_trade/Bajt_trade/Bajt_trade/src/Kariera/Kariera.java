package Kariera;

import Agent.Robotnik;
import Gielda.Gielda;
import Produkty.Narzedzia;
import Produkty.Ubrania;

import java.util.Collections;
import java.util.Vector;

abstract public class Kariera {
    public int poziom;
    protected final int id;
    protected final Robotnik r;
    protected final Gielda g;

    public Kariera(int poziom, int id, Robotnik r, Gielda g) {
        this.poziom = poziom;
        this.id = id;
        this.r = r;
        this.g = g;
    }

    public void ucz_sie() {
        poziom++;
    }
    public void zId(int id){
        if(id == 0)produkujUbrania();
        if(id == 1)produkujJedzenie();
        if(id == 2)produkujNarzedzia();
        if(id == 3)produkujProgramy();
        if(id == 4)produkujDiamenty();
    }
    public int liczModyfikator() {
        int modyfikator = 100;
        if (r.isCzyUbrany()) modyfikator -= g.getX();
        if (r.getDniNiejedzenia() == 1) modyfikator -= 100;
        if (r.getDniNiejedzenia() == 2) modyfikator -= 300;
        for (int i = 0; i < r.getNarzedzia().size(); i++) {
            modyfikator += r.getNarzedzia().get(i).poziom;
        }
        return Math.max(0, modyfikator);
    }

    public int liczModyfikatorZBonusem() {
        int modyfikator = 100;
        if (poziom == 1) modyfikator += 50;
        if (poziom == 2) modyfikator += 150;
        if (poziom == 3) modyfikator += 300;
        if (r.isCzyUbrany()) modyfikator -= g.getX();
        if (r.getDniNiejedzenia() == 1) modyfikator -= 100;
        if (r.getDniNiejedzenia() == 2) modyfikator -= 300;
        for (int i = 0; i < r.getNarzedzia().size(); i++) {
            modyfikator += r.getNarzedzia().get(i).poziom;
        }
        return Math.max(0, modyfikator);
    }


    public void produkujJedzenie() {
        int modyfikator = liczModyfikator();
        r.sprzedaj(1, modyfikator * r.dajProduktywnosc(1), 0);
       }

    public void produkujNarzedzia() {
        int modyfikator = liczModyfikator();
        if(modyfikator == 0)return;
        int ile_wyprodukuje = modyfikator * r.dajProduktywnosc(2) / 100;
        Vector<Narzedzia> wynik = new Vector<>();
        int k = Math.min(r.getProgramy().size(), ile_wyprodukuje);
        Collections.sort(r.getProgramy());
        for (int i = 0; i < k; i++) {
            wynik.add(new Narzedzia(r.getProgramy().get(r.getProgramy().size() - 1).poziom));
            r.getProgramy().remove(r.getProgramy().size() - 1);
            ile_wyprodukuje--;
        }
        for (int i = 0; i < ile_wyprodukuje; i++) {
            wynik.add(new Narzedzia(1));
        }
        int ilo = 1;
        for(int i = 1; i < wynik.size(); i++){
            if(wynik.get(i).poziom != wynik.get(i-1).poziom){
                r.sprzedaj(2, ilo, wynik.get(i-1).poziom);
                ilo = 1;
            }
            else ilo++;
        }
        r.sprzedaj(2, ilo, wynik.get(wynik.size()-1).poziom);
    }

    public void produkujUbrania() {
        int modyfikator = liczModyfikator();
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

    public void produkujProgramy(){
        int modyfikator = liczModyfikator();
        r.sprzedaj(3, modyfikator * r.dajProduktywnosc(3), poziom);
    }

    public void produkujDiamenty(){
        double modyfikator = liczModyfikator();
        r.zmienDiamenty(modyfikator * r.dajProduktywnosc(4)/100);
    }

    public int getId() {
        return id;
    }
}