package Gielda;

import Agent.Spekulant;

public class OfertaSprzedazy extends Oferta{
    public OfertaSprzedazy(Spekulant s, double cena, int idProduktu, int ilosc, int poziom) {
        super(s, cena, idProduktu, ilosc, poziom);
    }

    @Override
    public int compareTo(Oferta o) {
        if (this.poziom == o.poziom)
            if (this.cena > o.cena) return -1;
            else return 1;
        if (this.poziom < o.poziom)
            return -1;
        return 1;
    }
}
