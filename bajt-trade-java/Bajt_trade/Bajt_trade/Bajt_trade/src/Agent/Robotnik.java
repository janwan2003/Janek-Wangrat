package Agent;

import Gielda.*;
import Kariera.*;
import StrategiaKariery.*;
import StrategiaKupowania.*;
import StrategiaNauki.*;
import StrategiaProdukcji.*;

import java.util.Collections;

public class Robotnik extends Agent{
    private final int[] produktywnosc;
    public final StrategiaKariery strategiaKariery;
    private Kariera kariera;
    protected int dniNiejedzenia = 0;
    protected boolean czyUbrany = true;
    protected final int[] poziomy = {0, 0, 0, 0, 0};
    private final StrategiaNauki strategiaNauki;
    private final StrategiaKupowania strategiaKupowania;
    private final StrategiaProdukcji strategiaProdukcji;

    public int[] getProduktywnosc() {
        return produktywnosc;
    }
    public int dajProduktywnosc(int k){
        return produktywnosc[k];
    }

    public Robotnik(int id, double diamenty, int[] iloscZasobow, int[] produktywnosc, Gielda g, StrategiaKariery strategiaKariery, Kariera kariera, StrategiaNauki strategiaNauki, StrategiaKupowania strategiaKupowania, StrategiaProdukcji strategiaProdukcji) {
        super(id, diamenty, iloscZasobow, g);
        this.produktywnosc = produktywnosc;
        this.strategiaKariery = strategiaKariery;
        this.kariera = kariera;
        this.strategiaNauki = strategiaNauki;
        this.strategiaKupowania = strategiaKupowania;
        this.strategiaProdukcji = strategiaProdukcji;
    }

    public void sprzedaj(int id, int ilosc, int poziom){
        iloscOstatnioWyprodukowanych = ilosc;
        while(ilosc != 0){
            if(g.getOfertyKupna().get(id).size() == 0)break;

            OfertaKupna o = g.getOfertyKupna().get(id).get(g.getOfertyKupna().get(id).size()-1);
            int ileKupi = Math.min(o.getIlosc(), ilosc);
            o.getS().dodajZasob(id, ileKupi, poziom);
            o.getS().diamenty -= ileKupi * o.getCena();
            diamenty += ileKupi * o.getCena();
            o.odejmij(ileKupi);
            ilosc -= ileKupi;
            g.getIloscSprzedanych()[id] += ileKupi;
            g.getSumaCen()[id] += ileKupi * o.getCena();
            if(o.getIlosc() == 0) g.getOfertyKupna().get(id).remove(g.getOfertyKupna().get(id).size() - 1);
        }
        diamenty += g.getMinCeny()[id] * ilosc;
    }

    public Kariera getKariera() {
        return kariera;
    }

    public StrategiaNauki getStrategiaNauki() {
        return strategiaNauki;
    }

    public StrategiaKupowania getStrategiaKupowania() {
        return strategiaKupowania;
    }
    int iloscOstatnioWyprodukowanych = 0;

    public void przezyjDzien(){
        if(dniNiejedzenia == 3)return;
        if(g.getDzien() % 7 == 0){
            strategiaKariery.ZmienSciezke();
            dniNiejedzenia = 0;
            return;
        }
        if(strategiaNauki.czySieUczyc(this, g)){
            kariera.ucz_sie();
            dniNiejedzenia=0;
            return;
        }

        int coWyprodukowac = strategiaProdukcji.idProdukowanego();
        kariera.zId(coWyprodukowac);
        strategiaKupowania.kup();
        jedz();
        ubierz();
    }
    public void zmienSciezke(int k){
        if(k == this.kariera.getId()){
            kariera.ucz_sie();
            return;
        }
        this.poziomy[this.kariera.getId()] = this.kariera.poziom;
        if(k == 0){
            this.kariera = new Rzemieslnik(poziomy[0], this, g);
        }
        if(k == 1){
            this.kariera = new Rolnik(poziomy[1], this, g);
        }
        if(k == 2){
            this.kariera = new Inzynier(poziomy[2], this, g);
        }
        if(k == 3){
            this.kariera = new Programista(poziomy[3], this, g);
        }
        if(k == 4){
            this.kariera = new Gornik(poziomy[4], this, g);
        }
    }

    public void ubierz(){
        Collections.sort(this.ubrania);
        int a = ubrania.size();
        czyUbrany = a >= 100;
        for(int i = 0; i < 100; i++){
            if(ubrania.size() <= i)break;
            if(ubrania.get(i).poziom * ubrania.get(i).poziom == ubrania.get(i).getZuzycie() - 1){
                ubrania.remove(ubrania.size()-1);
            }
            else ubrania.get(i).uzyj();
        }
    }
    public void jedz(){
        if(iloscZasobow[1] >= 100)dniNiejedzenia = 0;
        else dniNiejedzenia++;
        int k = Math.min(iloscZasobow[1], 100);
        iloscZasobow[1] -= k;
        if(dniNiejedzenia == 3){
            diamenty = 0;
            for(int i = 0;i < 4; i++){
                iloscZasobow[i] = 0;
            }
            ubrania.clear();
            narzedzia.clear();
            programy.clear();
        }
    }

    public StrategiaKariery getStrategiaKariery() {
        return strategiaKariery;
    }

    public int getDniNiejedzenia() {
        return dniNiejedzenia;
    }

    public boolean isCzyUbrany() {
        return czyUbrany;
    }

    public int[] getPoziomy() {
        return poziomy;
    }

    public StrategiaProdukcji getStrategiaProdukcji() {
        return strategiaProdukcji;
    }

    public int getIloscOstatnioWyprodukowanych() {
        return iloscOstatnioWyprodukowanych;
    }
}
