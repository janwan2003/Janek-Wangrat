package pl.edu.mimuw.matrix;

public class Diagonal extends Matrix{
    protected double[] values;
    protected double othervalues;
    public Diagonal(double... v){
        this.othervalues = 0;
        this.shape = new Shape(v.length, v.length);
        this.values = new double[v.length];
        System.arraycopy(v, 0, this.values, 0, v.length);
    }

    public Diagonal(double ov, double... v){
        this.othervalues = ov;
        this.shape = new Shape(v.length, v.length);
        this.values = new double[v.length];
        System.arraycopy(v, 0, this.values, 0, v.length);
    }
    public double[][] data(){
        double[][] result = (new Constant(new Shape(this.shape.rows, this.shape.columns), this.othervalues)).data() ;
        for(int i = 0; i<this.values.length; i++){
            result[i][i] = this.values[i];
        }
        return result;
    }

    @Override
    public IDoubleMatrix times(IDoubleMatrix other){
        assert (this.shape().equals(other.shape()));
        double[][] A = other.data();
        double[][] result = new double[other.shape().rows][other.shape().columns];
        for(int i = 0; i < this.values.length; i++){
            for(int j = 0; j < this.values.length; j++){
                result[i][j] = A[i][j] * this.values[i];
            }
        }
        return new Full(result);
    }
    @Override
    public double normOne(){
        double k = 0;
        for (double value : this.values) {
            k = Math.max(k, Math.abs(value));
        }
        return k;
    }
    @Override
    public double normInfinity(){
        return this.normOne();
    }
    @Override
    public double frobeniusNorm(){
        double result = 0;
        for (double value : this.values) {
            result += value * value;
        }
        return Math.sqrt(result);
    }
    @Override
    public IDoubleMatrix plus(IDoubleMatrix m){
        assert(m.shape().rows == this.shape.rows && m.shape().columns == this.shape.columns);
        if(m.getClass() == Diagonal.class || m.getClass() == Identity.class){
            Diagonal other;
            other = (Diagonal) m;
            double[] result = new double[this.shape.rows];
            for(int i = 0; i < this.shape.rows; i++)result[i] = other.values[i] + this.values[i];
            return DoubleMatrixFactory.diagonal(result);
        }
        else if (m.getClass() == Constant.class) {
            Constant other;
            other = (Constant) m;
            double[] result = new double[this.shape.rows];
            for(int i = 0; i < this.shape.rows; i++)result[i] = other.giveValue() + this.values[i];
            return new Diagonal(this.othervalues + other.giveValue(), result);
        }
        else return super.plus(m);
    }

    @Override
    public IDoubleMatrix plus(double scalar){
            double[] result = new double[this.shape.rows];
            for(int i = 0; i < this.shape.rows; i++)result[i] = scalar + this.values[i];
            return new Diagonal(scalar + this.othervalues, result);
    }
    @Override
    public String toString(){
        if(this.shape.rows <= 3)return super.toString();
        StringBuilder result = new StringBuilder();
        result.append(this.rozmiarMacierzy());
        for(int i = 0; i < this.shape.rows; i++){
            for(int j = 0; j < this.shape.columns; j++){
                if(Math.abs(i-j)<=1 || i == 0 || i == this.shape.columns-1 || j ==0 || j==this.shape.columns-1)
                    result.append(this.data()[i][j]).append(" ");
                else if(Math.abs(i-j) == 2)
                    result.append("... ");
                else result.append("    ");
            }
            result.append("\n");
        }
        return result.toString();
    }
}
