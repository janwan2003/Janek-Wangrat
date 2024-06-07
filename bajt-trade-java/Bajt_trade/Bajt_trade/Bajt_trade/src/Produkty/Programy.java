package Produkty;

public class Programy extends Produkt{
    public Programy(int poz){
        this.poziom = poz;
        this.id = 3;
    }

    static public Programy[] dajTabliceProduktu(int ilosc, int poz){
        Programy[] t = new Programy[ilosc];
        for(int i = 0; i < ilosc; i++){
            t[i] = new Programy(poz);
        }
        return t;
    }
}

