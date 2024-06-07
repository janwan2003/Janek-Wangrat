package pl.edu.mimuw.matrix;

public class DoubleMatrixFactory {

  private DoubleMatrixFactory() {
  }

  public static IDoubleMatrix sparse(Shape shape, MatrixCellValue... values){
    return new Sparse(shape, values);
  }

  public static IDoubleMatrix full(double[][] values) {
    return new Full(values);
  }

  public static IDoubleMatrix identity(int size) {
    return new Identity(size);
  }

  public static IDoubleMatrix diagonal(double... diagonalValues) {
    return new Diagonal(diagonalValues);
  }

  public static IDoubleMatrix antiDiagonal(double... antiDiagonalValues) {
    return new AntiDiagonal(antiDiagonalValues);
  }

  public static IDoubleMatrix vector(double... values){
    return new Vector(values);
  }

  public static IDoubleMatrix zero(Shape shape) {
    return new Zero (shape);
  }

  public static IDoubleMatrix column(Shape shape, double... values){
    return new Column(shape, values);
  }

  public static IDoubleMatrix row(Shape shape, double... values){
    return new Row(shape, values);
  }

  public static IDoubleMatrix constant(Shape shape, double value){
    return new Constant(shape, value);
  }

}
