package Gielda;

public class Socjalistyczna extends Gielda {


    public Socjalistyczna(int x, double[] c) {
        super(x, c);
    }

    @Override
    public void przeprowadzDzien(){
        for(int i = 0; i < 4; i++){
            sumaCen[i] = 0;
            iloscSprzedanych[i] = 0;
            ilosc.get(i).add(0);
        }
        dzien++;
        for(int i = spekulanci.size() - 1; i >= 0; i--){
            spekulanci.get(i).wystawOferty(this);
        }
        for(int i = robotnicy.size() - 1; i >= 0; i--){
            robotnicy.get(i).przezyjDzien();
        }
        for(int i = 0; i < 4; i++){
            ceny.get(i).add(sumaCen[i] / iloscSprzedanych[i]);
            ofertyKupna.get(i).clear();
            ofertySprzedazy.get(i).clear();
        }

    }
}
