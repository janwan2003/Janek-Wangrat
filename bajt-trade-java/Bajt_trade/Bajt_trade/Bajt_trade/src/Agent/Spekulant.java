package Agent;

import Gielda.*;
import Produkty.*;

import java.util.Vector;

public abstract class Spekulant extends Agent{

    public Spekulant(int id, double diamenty, int[] iloscZasobow, Gielda g) {
        super(id, diamenty, iloscZasobow, g);
    }

    public void wystawOferteKupna(Gielda g, double cena, int idProduktu){
        if (diamenty/cena < 100)return;
        g.getMinCeny()[idProduktu] = Math.min(g.getMinCeny()[idProduktu], cena);
        OfertaKupna o = new OfertaKupna(this, cena, idProduktu, 100, 0);
        g.getOfertyKupna().get(idProduktu).add(o);
    }
    public void wystawOferteSprzedazy(Gielda g, double cena, int idProduktu, int ilosc, int poziom){
        OfertaSprzedazy o = new OfertaSprzedazy(this, cena, idProduktu, ilosc, poziom);
        g.getIlosc().get(idProduktu).set(g.getDzien(), g.getIlosc().get(idProduktu).get(g.getDzien())+100);
        g.getOfertySprzedazy().get(idProduktu).add(o);
    }
    abstract public void wystawOferty(Gielda g);

    public void sprzedaj(Gielda g, Vector<? extends Produkt> v, double cena){
        if(v.size() == 0)return;
        int idP = v.get(0).getId();
        int ilo = 1;
        for(int i = 1; i < v.size(); i++){
            if(v.get(i).poziom != v.get(i-1).poziom){
                wystawOferteSprzedazy(g, cena, idP, ilo, v.get(i-1).poziom);
                ilo = 1;
            }
            else ilo++;
        }
        wystawOferteSprzedazy(g, cena, idP, ilo, v.get(v.size()-1).poziom);
    }
}
