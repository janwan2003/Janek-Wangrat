import Gielda.*;
import Agent.*;
import Kariera.*;
import Produkty.*;
import StrategiaKariery.*;
import StrategiaNauki.*;
import StrategiaProdukcji.*;
import StrategiaKupowania.*;

public class Main {
    public static void main(String[] args){
        Kapitalistyczna gie = new Kapitalistyczna(5, new double[]{1, 2, 3 ,4});
        Robotnik rob = null;
        rob = new Robotnik(
                1,
                420.420,
                new int[]{100, 100, 100, 100},
                new int[]{100, 100, 100, 100, 100},
                gie,
                new Konserwatysta(rob, gie),
                new Gornik(1, rob, gie),
                new Student(4, 4),
                new Czyscioszek(rob, gie),
                new Chciwy(gie, rob)
        );
        gie.dodajRobotnika(rob);
        Spekulant spencer = new Sredni(
                2137,
                128.256,
                new int[]{100, 200, 300, 400},
                gie,
                12
        );
        gie.dodajSpekulanta(spencer);
        gie.przeprowadzDzien();
    }
}
