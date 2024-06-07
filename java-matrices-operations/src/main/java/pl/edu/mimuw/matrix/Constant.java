package pl.edu.mimuw.matrix;

public class Constant extends Matrix{
    private final double value;
    public Constant(Shape s, double v){
        this.shape = pl.edu.mimuw.matrix.Shape.matrix(s.rows,s.columns);
        this.value = v;
    }
    public double giveValue(){
        return this.value;
    }

    @Override
    public double[][] data() {
        double[][] result = new double[this.shape.rows][this.shape.columns];
        for(int i = 0; i < this.shape.rows; i++){
            for(int j = 0; j < this.shape.columns; j++){
                result[i][j] = this.value;
            }
        }
        return result;
    }

    @Override
    public IDoubleMatrix times (IDoubleMatrix m){
        assert(this.shape.columns == m.shape().rows);
        if(m.getClass() == Constant.class){
            Constant k;
            k = (Constant) m;
            Shape newShape = new Shape(m.shape().rows, this.shape.columns);
            return new Constant(newShape, this.value * k.value * shape.rows);
        }
        else{
            double[][] A = m.data();
            double[][] result = new double[m.shape().rows][m.shape().columns];
            for(int i=0; i<m.shape().columns; i++){
                for(int j=0; j<m.shape().rows; j++){
                    result[0][i] += A[j][i] * this.value;
                }
            }
            for(int i=0; i<m.shape().columns; i++){
                for(int j=0; j<m.shape().rows; j++){
                    result[j][i] = result[0][i];
                }
            }
            return new Full(result);
        }
    }
    @Override
    public IDoubleMatrix plus(double v){return new Constant(this.shape, this.value + v);}
    @Override
    public IDoubleMatrix minus(double v){return this.plus(-v);}
    //zauważmy, że constant +- constant zwraca constant
    @Override
    public IDoubleMatrix plus(IDoubleMatrix other){
        return other.plus(this.value);
    }
    @Override
    public IDoubleMatrix minus(IDoubleMatrix other){
        return other.minus(this.value);
    }
    @Override
    public double normOne(){
        return this.value;
    }
    @Override
    public double normInfinity(){
        return this.value;
    }
    @Override
    public double frobeniusNorm(){
        return Math.sqrt(this.shape.rows * this.shape.columns * Math.abs(this.value));
    }
    @Override
    public String toString(){
        if(this.shape.rows <= 3 || this.shape.columns <= 3)return super.toString();
        return this.rozmiarMacierzy() +
                this.value +
                " ... " +
                this.value +
                "\n" +
                "... ".repeat(3) +
                "\n" +
                this.value +
                " ... " +
                this.value +
                '\n';
    }
}
