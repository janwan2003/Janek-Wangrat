package Agent;

import Gielda.Gielda;

public class Sredni extends Spekulant{
    private final int parametr;

    public Sredni(int id, double diamenty, int[] iloscZasobow, Gielda g, int parametr) {
        super(id, diamenty, iloscZasobow, g);
        this.parametr = parametr;
    }

    @Override
    public void wystawOferty(Gielda g) {
        for(int i = 0; i < 4; i++) {
            double srCena = g.liczSrednieCeny(g.getCeny().get(i), parametr);
            if (iloscZasobow[i] == 0) {
                wystawOferteKupna(g, (srCena * 0.95), i);
            }
            else
                wystawOferteKupna(g, (srCena * 0.9), i);
        }
        double srCenaJedzenia = g.liczSrednieCeny(g.getCeny().get(1), parametr);
        wystawOferteSprzedazy(g, (srCenaJedzenia * 1.10), 1, iloscZasobow[1], 0);

        double srCenaUbran = g.liczSrednieCeny(g.getCeny().get(0), parametr);
        sprzedaj(g, ubrania, srCenaUbran);
        double srCenaNarzedzi = g.liczSrednieCeny(g.getCeny().get(2), parametr);
        sprzedaj(g, narzedzia, srCenaNarzedzi);
        double srCenaProgramow = g. liczSrednieCeny(g.getCeny().get(3), parametr);
        sprzedaj(g, programy, srCenaProgramow);
    }
}
