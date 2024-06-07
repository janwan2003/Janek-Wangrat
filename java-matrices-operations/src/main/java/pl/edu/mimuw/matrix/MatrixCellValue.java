package pl.edu.mimuw.matrix;

public final class MatrixCellValue implements Comparable<MatrixCellValue> {

  public final int row;
  public final int column;
  public final double value;

  private MatrixCellValue(int row, int column, double value) {
    this.column = column;
    this.row = row;
    this.value = value;
  }

  @Override
  public String toString() {
    return "{" + value + " @[" + row + ", " + column + "]}";
  }

  public static MatrixCellValue cell(int row, int column, double value) {
    return new MatrixCellValue(row, column, value);
  }

  @Override
  public int compareTo(MatrixCellValue b){
      if(this.row > b.row) return 1;
      if(this.row < b.row) return -1;
      else{
        return Integer.compare(this.column, b.column);
      }
  }
}
