package Gielda;

public class Zrownowazona extends Gielda {


    public Zrownowazona(int x, double[] c) {
        super(x, c);
    }

    @Override
    public void przeprowadzDzien(){
        for(int i = 0; i < 4; i++){
            sumaCen[i] = 0;
            iloscSprzedanych[i] = 0;
        }
        dzien++;
        for(int i = 0; i < spekulanci.size(); i++){
            if(i % 2 == 0){
                spekulanci.get(2*i).wystawOferty(this);
            }
        }
        for(int i = 0; i < robotnicy.size(); i++){
            if(i % 2 == 0){
                robotnicy.get(2*i).przezyjDzien();
            }
        }
        for(int i = 0; i < 4; i++){
            ceny.get(i).add(sumaCen[i] / iloscSprzedanych[i]);
            ofertyKupna.get(i).clear();
            ofertySprzedazy.get(i).clear();
        }

    }
}
