package Gielda;

import java.util.Vector;
import Agent.*;
abstract public class Gielda {
    protected final int x;
    protected final Vector<Vector<Double>> ceny;
    protected final Vector<Vector<Integer>> ilosc;
    protected final double[] minCeny;
    protected final Vector<Vector<OfertaKupna>> ofertyKupna;
    protected final Vector<Vector<OfertaSprzedazy>> ofertySprzedazy;
    protected int dzien;
    protected Vector<Robotnik> robotnicy = new Vector<>();
    protected Vector<Spekulant> spekulanci = new Vector<>();
    protected double[] sumaCen = new double[4];
    protected double[] iloscSprzedanych = new double[4];
    public Gielda(int x, double[] c) {
        this.x = x;
        this.dzien = 0;
        this.ilosc = new Vector<>();
        this.minCeny = new double[4];
        System.arraycopy(c, 0, minCeny, 0, 4);
        this.ceny = new Vector<>();
        this.ofertyKupna = new Vector<>();
        this.ofertySprzedazy = new Vector<>();

        for(int i = 0; i < 4; i++){
            Vector<Double> pom = new Vector<>();
            Vector<Integer> pom2 = new Vector<>();
            pom.add(c[i]);
            pom2.add(1);
            this.ofertyKupna.add(new Vector<>());
            this.ofertySprzedazy.add(new Vector<>());
            this.ceny.add(pom);
            this.ilosc.add(pom2);
            this.ilosc.add(pom2);
        }
    }

    public double liczSrednieCeny(Vector<Double> v, int l_dni){
        int k = Math.min(v.size(), l_dni);
        int sr = 0;
        for(int i = 0; i < k; i++){
            sr += v.get(i);
        }
        sr /= k;
        return sr;
    }

   public int liczSumeOfert(Vector<Integer> v, int l_dni){
        int suma = 0;
        int k = Math.min(l_dni, dzien - 1);
        for(int i = 0; i < k; i++){
            suma += v.get(v.size() - 1 - i);
        }
        return suma;
   }

   abstract public void przeprowadzDzien();

    public int getX() {
        return x;
    }

    public Vector<Vector<Double>> getCeny() {
        return ceny;
    }

    public Vector<Vector<Integer>> getIlosc() {
        return ilosc;
    }

    public double[] getMinCeny() {
        return minCeny;
    }

    public Vector<Vector<OfertaKupna>> getOfertyKupna() {
        return ofertyKupna;
    }

    public Vector<Vector<OfertaSprzedazy>> getOfertySprzedazy() {
        return ofertySprzedazy;
    }

    public int getDzien() {
        return dzien;
    }

    public Vector<Robotnik> getRobotnicy() {
        return robotnicy;
    }

    public Vector<Spekulant> getSpekulanci() {
        return spekulanci;
    }

    public double[] getSumaCen() {
        return sumaCen;
    }

    public double[] getIloscSprzedanych() {
        return iloscSprzedanych;
    }
    public void dodajSpekulanta(Spekulant s){
        this.spekulanci.add(s);
    }
    public void dodajRobotnika(Robotnik r){
        this.robotnicy.add(r);
    }
}
