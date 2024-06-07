package Agent;

import Gielda.*;
import Produkty.*;
import java.util.Objects;
import java.util.Vector;

abstract public class Agent {
    protected final Gielda g;
    protected final int id;
    protected double diamenty;
    protected Vector<Ubrania> ubrania = new Vector<>();
    protected final int[] iloscZasobow;
    protected Vector<Narzedzia> narzedzia = new Vector<>();
    protected Vector<Programy> programy = new Vector<>();

    public Agent(int id, double diamenty, int[] iloscZasobow, Gielda g) {
        this.g = g;
        this.id = id;
        this.diamenty = diamenty;
        for(int i = 0; i < iloscZasobow[0]; i++) Objects.requireNonNull(ubrania).add(new Ubrania(1));
        for(int i = 0; i < iloscZasobow[2]; i++) Objects.requireNonNull(narzedzia).add(new Narzedzia(1));
        for(int i = 0; i < iloscZasobow[3]; i++) Objects.requireNonNull(programy).add(new Programy(1));
        this.iloscZasobow = iloscZasobow;
    }

    public void dodajZasob(int id, int ilosc, int poziom){
        if(id == 1)iloscZasobow[1]+=ilosc;
        if(id == 0){
            iloscZasobow[0]+=ilosc;
            for(int i = 0; i < ilosc; i++)
                ubrania.add(new Ubrania(poziom));
        }
        if(id == 2){
            iloscZasobow[2]+=ilosc;
            for(int i = 0; i < ilosc; i++) {
                narzedzia.add(new Narzedzia(poziom));
            }
        }
        if(id == 3){
            iloscZasobow[3]+=ilosc;
            for(int i = 0; i < ilosc; i++){
                programy.add(new Programy(poziom));
            }
        }
        if(id == 4){
            diamenty += ilosc;
        }
    }

    public void usunZasob(int id, int ilosc, int poziom){
        if(id == 1)iloscZasobow[1]-=ilosc;
        if(id == 0){
            iloscZasobow[0] -= ilosc;
            int usunieto = 0;
            int i = 0;
            while(usunieto != ilosc){
                if(ubrania.get(i).poziom != poziom)i++;
                ubrania.remove(i);
                usunieto++;
            }
        }
        if(id == 2){
            iloscZasobow[2] -= ilosc;
            int usunieto = 0;
            int i = 0;
            while(usunieto != ilosc){
                if(narzedzia.get(i).poziom != poziom)i++;
                narzedzia.remove(i);
                usunieto++;
            }
        }
        if(id == 3){
            iloscZasobow[0] -= ilosc;
            int usunieto = 0;
            int i = 0;
            while(usunieto != ilosc){
                if(programy.get(i).poziom != poziom)i++;
                programy.remove(i);
                usunieto++;
            }
        }
        if(id == 4)diamenty-=ilosc;
    }

    public Gielda getG() {
        return g;
    }

    public int getId() {
        return id;
    }

    public double getDiamenty() {
        return diamenty;
    }

    public Vector<Ubrania> getUbrania() {
        return ubrania;
    }

    public int[] getIloscZasobow() {
        return iloscZasobow;
    }

    public Vector<Narzedzia> getNarzedzia() {
        return narzedzia;
    }

    public Vector<Programy> getProgramy() {
        return programy;
    }
    public void zmienDiamenty(double a){
        diamenty += a;
    }
}
