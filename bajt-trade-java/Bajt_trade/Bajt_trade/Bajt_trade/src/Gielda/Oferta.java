package Gielda;

import Agent.Spekulant;

abstract public class Oferta implements Comparable<Oferta>{
    protected final Spekulant s;

    public Oferta(Spekulant s, double cena, int idProduktu, int ilosc, int poziom) {
        this.s = s;
        this.cena = cena;
        this.idProduktu = idProduktu;
        this.ilosc = ilosc;
        this.poziom = poziom;
    }

    protected final double cena;
    protected final int idProduktu;
    protected int ilosc;
    protected final int poziom;

    public Spekulant getS() {
        return s;
    }

    public double getCena() {
        return cena;
    }

    public int getIdProduktu() {
        return idProduktu;
    }

    public void odejmij(int a){
        ilosc -= a;
    }

    public int getPoziom() {
        return poziom;
    }

    public int getIlosc() {
        return ilosc;
    }
}
