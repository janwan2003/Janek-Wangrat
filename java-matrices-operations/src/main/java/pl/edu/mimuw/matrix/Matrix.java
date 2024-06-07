package pl.edu.mimuw.matrix;
import java.lang.Math;

abstract public class Matrix implements IDoubleMatrix{
    protected Shape shape;
    public IDoubleMatrix times(IDoubleMatrix other){
        assert(this.shape.columns == other.shape().rows);
        double[][] A = this.data();
        double[][] B = other.data();
        double[][] C = new double[this.shape.rows][other.shape().columns];
        for(int i = 0; i < this.shape.rows; i++){
            for(int j = 0; j < other.shape().columns; j++){
                double wynik = 0;
                for(int k = 0; k < this.shape.columns; k++){
                    wynik += A[i][k] * B[k][j];
                }
                C[i][j] = wynik;
            }
        }
        return new Full(C);
    }

    public IDoubleMatrix times(double scalar){
        double[][] A = this.data();
        for(int i = 0; i < this.shape.rows; i++){
            for(int j = 0; j < this.shape.columns; j++){
                A[i][j] *= scalar;
            }
        }
        return new Full(A);
    }

    public IDoubleMatrix plus(IDoubleMatrix other){
        assert (this.shape.equals(other.shape()));
        double[][] A = this.data();
        double[][] B = other.data();
        for(int i = 0; i < this.shape.rows; i++){
            for(int j = 0; j < this.shape.columns; j++){
                A[i][j] += B[i][j];
            }
        }
        return new Full(A);
    }

    public IDoubleMatrix plus(double scalar){
        double[][] A = this.data();
        for(int i = 0; i < this.shape.rows; i++){
            for(int j = 0; j < this.shape.columns; j++){
                A[i][j] += scalar;
            }
        }
        return new Full(A);
    }

    public IDoubleMatrix minus(IDoubleMatrix other){
        assert (this.shape.equals(other.shape()));
        return this.plus(other.times(-1));
    }

    public IDoubleMatrix minus(double scalar){
        return this.plus(-scalar);
    }

    public double get(int row, int column){
        shape.assertInShape(row, column);
        return this.data()[row][column];
    }

    abstract public double[][] data();

    public double normOne(){
        double[][] A = this.data();
        double result = 0;
        double maximum = 0;
        for(int i = 0; i < this.shape.columns; i++){
            for(int j = 0; j < this.shape.rows; j++)result+=Math.abs(A[j][i]);
            if(result > maximum)maximum = result;
            result = 0;
        }
        return maximum;
    }

    public double normInfinity(){
        double[][] A = this.data();
        double result = 0;
        double maximum = -10000;
        for(int i = 0; i < this.shape.rows; i++){
            for(int j = 0; j < this.shape.columns; j++)result+=Math.abs(A[i][j]);
            if(result > maximum)maximum = result;
            result = 0;
        }
        return maximum;
    }

    public double frobeniusNorm(){
        double result = 0;
        for(int i = this.shape.rows - 1; i >= 0; i--){
            for(int j = 0; j < this.shape.columns; j++){
                result += this.data()[i][j] * this.data()[i][j];
            }
        }
        return Math.sqrt(result);
    }

    public Shape shape(){
        return this.shape;
    }
    public String rozmiarMacierzy(){
        return "Nasza macierz jest rozmiaru " +
                this.shape.rows +
                "x" +
                this.shape.columns +
                ", jej komórki to:\n";
    }
    @Override
    public String toString(){
        StringBuilder result = new StringBuilder();
        result.append(this.rozmiarMacierzy());
        for(int i = 0; i < this.shape.rows; i++){
            for(int j = 0; j < this.shape.columns; j++){
                result.append(this.data()[i][j]).append(" ");
            }
            result.append("\n");
        }
        return result.toString();
    }
}
