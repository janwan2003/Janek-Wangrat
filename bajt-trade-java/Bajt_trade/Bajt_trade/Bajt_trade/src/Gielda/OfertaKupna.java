package Gielda;

import Agent.Spekulant;

public class OfertaKupna extends Oferta{
    public OfertaKupna(Spekulant s, double cena, int idProduktu, int ilosc, int poziom) {
        super(s, cena, idProduktu, ilosc, poziom);
    }

    @Override
    public int compareTo(Oferta other){
        return (int) ((this.cena - other.cena) * 100);
    }
}
