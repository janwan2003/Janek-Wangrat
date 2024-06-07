package Agent;

import Gielda.Gielda;

public class Wypukly extends Spekulant{
    public Wypukly(int id, double diamenty, int[] iloscZasobow, Gielda g) {
        super(id, diamenty, iloscZasobow, g);
    }

    public boolean czyWypukla(Gielda g, int idP){
        if(g.getDzien() < 3)return true;
        return g.getCeny().get(idP).get(g.getCeny().get(idP).size() - 1) + g.getCeny().get(idP).get(g.getCeny().get(idP).size() - 3)
                >=
                2 * g.getCeny().get(idP).get(g.getCeny().get(idP).size() - 2);
    }
    @Override
    public void wystawOferty(Gielda g) {
        for(int i = 0; i < 4; i++){
            if(!czyWypukla(g, i))continue;
            wystawOferteKupna(g,  (0.9 * g.getCeny().get(i).get(g.getDzien() - 1)), i);
        }

        if(czyWypukla(g, 0)){
            sprzedaj(g, ubrania,  (1.1 * g.getCeny().get(0).get(g.getCeny().get(0).size() - 1)));
        }
        if(czyWypukla(g, 1)){
            wystawOferteSprzedazy(g,  (g.getCeny().get(1).get(g.getCeny().get(1).size() - 1) * 1.1), 1, iloscZasobow[1], 0);
        }
        if(czyWypukla(g, 2)){
            sprzedaj(g, narzedzia,  (1.1 * g.getCeny().get(2).get(g.getCeny().get(2).size() - 1)));
        }
        if(czyWypukla(g, 3)){
            sprzedaj(g, programy,  (1.1 * g.getCeny().get(3).get(g.getCeny().get(3).size() - 1)));
        }
    }
}
