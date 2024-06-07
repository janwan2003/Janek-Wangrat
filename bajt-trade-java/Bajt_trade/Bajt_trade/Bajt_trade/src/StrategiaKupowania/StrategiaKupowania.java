package StrategiaKupowania;

import Agent.*;
import Gielda.*;
import Produkty.Narzedzia;
import Produkty.Programy;
import Produkty.Ubrania;

abstract public class StrategiaKupowania {
    final Robotnik r;
    final Gielda g;

    public StrategiaKupowania(Robotnik r, Gielda g) {
        this.r = r;
        this.g = g;
    }

    abstract public void kup();

    public void kupJedzenie(){
        int zakupioneJedzenie = 0;
        while(zakupioneJedzenie != 100 && g.getOfertySprzedazy().get(1).size() != 0){
            OfertaSprzedazy aktO= g.getOfertySprzedazy().get(1).get(g.getOfertySprzedazy().get(1).size()-1);
            int ileMozeKupic = (int) (r.getDiamenty() / aktO.getCena());
            int ileKupi = Math.min(Math.min(ileMozeKupic, 100 - zakupioneJedzenie), aktO.getIlosc());
            r.zmienDiamenty(-1 * ileKupi * aktO.getCena());
            aktO.getS().zmienDiamenty(ileKupi * aktO.getCena());
            aktO.odejmij(ileKupi);
            r.getIloscZasobow()[1] += ileKupi;
            aktO.getS().getIloscZasobow()[1] -= ileKupi;
            g.getIloscSprzedanych()[1] += ileKupi;
            zakupioneJedzenie += ileKupi;
            g.getSumaCen()[1] += ileKupi * aktO.getCena();
            if(aktO.getIlosc() == 0) g.getOfertySprzedazy().get(1).remove(g.getOfertySprzedazy().get(1).size()-1);
            else break;
        }
    }

    public void kupUbrania(){
        int posiadaneUbrania = Math.min(r.getUbrania().size(), 100);
        int j = g.getOfertySprzedazy().get(0).size()-1;
        while(posiadaneUbrania != 100 && g.getOfertySprzedazy().get(0).size() != 0 && j != -1){
            OfertaSprzedazy aktO= g.getOfertySprzedazy().get(0).get(j);
            int ileMozeKupic = (int) (r.getDiamenty() / aktO.getCena());
            int ileKupi = Math.min(Math.min(ileMozeKupic, 100 - posiadaneUbrania), aktO.getIlosc());
            r.zmienDiamenty(-ileKupi * aktO.getCena());
            aktO.getS().zmienDiamenty(ileKupi * aktO.getCena());
            aktO.odejmij(ileKupi);
            g.getIloscSprzedanych()[0] += ileKupi;
            g.getSumaCen()[0] += ileKupi * aktO.getCena();
            posiadaneUbrania += ileKupi;
            for(int i = 0; i < ileKupi; i++){
                r.getUbrania().add(new Ubrania(aktO.getPoziom()));
            }
            r.getIloscZasobow()[0] = r.getUbrania().size();
            aktO.getS().getIloscZasobow()[0] -= ileKupi;
            aktO.getS().usunZasob(0, ileKupi, aktO.getPoziom());
            if(aktO.getIlosc() == 0) g.getOfertySprzedazy().get(0).remove(g.getOfertySprzedazy().get(0).size()-1);
        }
    }

    public void kupNarzedzia(int k){
        int kupioneNarzedzia = 0;
        int j = g.getOfertySprzedazy().get(2).size()-1;
        while(kupioneNarzedzia != k && g.getOfertySprzedazy().get(2).size() != 0 && j != -1){
            OfertaSprzedazy aktO= g.getOfertySprzedazy().get(2).get(j);
            int ileMozeKupic = (int) (r.getDiamenty() / aktO.getCena());
            int ileKupi = Math.min(Math.min(ileMozeKupic, k - kupioneNarzedzia), aktO.getIlosc());
            r.zmienDiamenty(- ileKupi * aktO.getCena());
            aktO.getS().zmienDiamenty(ileKupi * aktO.getCena());
            aktO.odejmij(ileKupi);
            g.getIloscSprzedanych()[2] += ileKupi;
            g.getSumaCen()[2] += ileKupi * aktO.getCena();
            kupioneNarzedzia += ileKupi;
            for(int i = 0; i < ileKupi; i++){
                r.getNarzedzia().add(new Narzedzia(aktO.getPoziom()));
            }
            r.getIloscZasobow()[2] = r.getNarzedzia().size();
            aktO.getS().getIloscZasobow()[2] -= ileKupi;
            aktO.getS().usunZasob(2, ileKupi, aktO.getPoziom());
            if(aktO.getIlosc() == 0) g.getOfertySprzedazy().get(2).remove(g.getOfertySprzedazy().get(2).size()-1);
        }
    }

    public void kupProgramy(){
        int kupioneProgramy = 0;
        int ileKupic = r.getIloscOstatnioWyprodukowanych();
        int j = g.getOfertySprzedazy().get(3).size()-1;
        while(kupioneProgramy != ileKupic && g.getOfertySprzedazy().get(3).size() != 0 && j != -1){
            OfertaSprzedazy aktO= g.getOfertySprzedazy().get(3).get(j);
            int ileMozeKupic = (int) (r.getDiamenty() / aktO.getCena());
            int ileKupi = Math.min(Math.min(ileMozeKupic, ileKupic - kupioneProgramy), aktO.getIlosc());
            r.zmienDiamenty(-ileKupi * aktO.getCena());
            aktO.getS().zmienDiamenty( ileKupi * aktO.getCena());
            aktO.odejmij(ileKupi);
            g.getIloscSprzedanych()[3] += ileKupi;
            g.getSumaCen()[3] += ileKupi * aktO.getCena();
            kupioneProgramy += ileKupi;
            for(int i = 0; i < ileKupi; i++){
                r.getProgramy().add(new Programy(aktO.getPoziom()));
            }
            r.getIloscZasobow()[3] = r.getProgramy().size();
            aktO.getS().getIloscZasobow()[3] -= ileKupi;
            aktO.getS().usunZasob(3, ileKupi, aktO.getPoziom());
            if(aktO.getIlosc() == 0) g.getOfertySprzedazy().get(3).remove(g.getOfertySprzedazy().get(3).size()-1);
        }
    }
}