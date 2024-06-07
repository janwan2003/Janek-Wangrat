package pl.edu.mimuw;

import pl.edu.mimuw.matrix.*;

import static pl.edu.mimuw.matrix.DoubleMatrixFactory.*;
import static pl.edu.mimuw.matrix.MatrixCellValue.cell;
import static pl.edu.mimuw.matrix.Shape.matrix;

public class Main {
      public static void main(String[] args) {
      final double[] v = new double[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
      final double[][] fu = new double[10][10];
      fu[0]=v;
      Shape s = new Shape(10, 10);
      IDoubleMatrix antiDiagonal = antiDiagonal(v);
      IDoubleMatrix Column = column(s, v);
      IDoubleMatrix Constant = constant(s, 2);
      IDoubleMatrix Diagonal = diagonal(v);
      IDoubleMatrix Full = full(fu);
      IDoubleMatrix Identity = identity(10);
      IDoubleMatrix Row = row(s, v);
      IDoubleMatrix Sparse = sparse(s,
        cell(1,1,3),
        cell(2,3,4),
        cell(3,4,5)
        );
      IDoubleMatrix times = Constant.times(Constant);
      IDoubleMatrix timesScalar = Column.times(-42);
      IDoubleMatrix plus = Diagonal.plus(Identity);
      IDoubleMatrix plusValue = Identity.plus(3);
      IDoubleMatrix minus = Column.minus(Full);
      IDoubleMatrix minusValue = Constant.minus(2137);
      double normone = Row.normOne();
      double norminfinity = Row.normInfinity();
      double frobeniusnorm = Row.frobeniusNorm();

      IDoubleMatrix Vector = vector(v);
      IDoubleMatrix Zero = zero(s);
            System.out.println("AntiDiagonal");
            System.out.println(antiDiagonal);
            System.out.println("Column");
            System.out.println(Column);
            System.out.println("Constant");
            System.out.println(Constant);
            System.out.println("Diagonal");
            System.out.println(Diagonal);
            System.out.println("Full");
            System.out.println(Full);
            System.out.println("Identity");
            System.out.println(Identity);
            System.out.println("Row");
            System.out.println(Row);
            System.out.println("Sparse");
            System.out.println(Sparse);
            System.out.println("Vector");
            System.out.println(Vector);
            System.out.println("Zero");
            System.out.println(Zero);
            System.out.println();

            System.out.println("times");
            System.out.println(times);
            System.out.println("timesScalar");
            System.out.println(timesScalar);
            System.out.println("plus");
            System.out.println(plus);
            System.out.println("plusValue");
            System.out.println(plusValue);
            System.out.println("minus");
            System.out.println(minus);
            System.out.println("minusValue");
            System.out.println(minusValue);
            System.out.println("normOne");
            System.out.println(normone);
            System.out.println("normInfinity");
            System.out.println(norminfinity);
            System.out.println("frobeniusNorm");
            System.out.println(frobeniusnorm);


      }
}

