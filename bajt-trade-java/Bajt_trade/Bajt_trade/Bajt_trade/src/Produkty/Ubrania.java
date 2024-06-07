package Produkty;

public class Ubrania extends Produkt{
    protected int zuzycie = 0;
    public Ubrania(int poz){
        this.poziom=poz;
    }
    public void uzyj(){
        zuzycie++;
        this.id = 0;
    }

    static public Ubrania[] dajTabliceProduktu(int ilosc, int poz){
        Ubrania[] t = new Ubrania[ilosc];
        for(int i = 0; i < ilosc; i++){
            t[i] = new Ubrania(poz);
        }
        return t;
    }
    @Override
    public int compareTo(Produkt m){
        if(this.getClass() == Ubrania.class) {
            Ubrania otherUbrania = (Ubrania) m;
            return (this.poziom * this.poziom - this.zuzycie)
                    - (otherUbrania.poziom * otherUbrania.poziom - otherUbrania.zuzycie);
        }
        else return super.compareTo(m);
    }

    public int getZuzycie() {
        return zuzycie;
    }
}
