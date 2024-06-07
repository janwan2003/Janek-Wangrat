package Produkty;

abstract public class Produkt implements Comparable<Produkt> {
    protected int id;
    public int poziom;
   // abstract  public Produkty.Produkt[] dajTabliceProduktu(int ilosc, int poziom);
    @Override
    public int compareTo(Produkt otherProdukt){
        return this.poziom - otherProdukt.poziom;
    }

    public int getId() {
        return id;
    }

    public int getPoziom() {
        return poziom;
    }
}
