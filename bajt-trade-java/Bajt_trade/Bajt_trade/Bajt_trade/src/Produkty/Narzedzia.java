package Produkty;

public class Narzedzia extends Produkt{
    private final String nazwa = "narzedzia";
    public Narzedzia(int poz){
        this.poziom = poz;
        this.id = 2;
    }
    static public Narzedzia[] dajTabliceProduktu(int ilosc, int poz){
        Narzedzia[] t = new Narzedzia[ilosc];
        for(int i = 0; i < ilosc; i++){
            t[i] = new Narzedzia(poz);
        }
        return t;
    }
}

