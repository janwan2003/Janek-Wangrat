package pl.edu.mimuw.matrix;

public class Row extends Matrix{
    private final double[] values;
    public Row(Shape s,double[] v){
        assert(s.rows == v.length);
        this.values = new double[v.length];
        System.arraycopy(v, 0, this.values, 0, v.length);
        this.shape = pl.edu.mimuw.matrix.Shape.matrix(s.rows,s.columns);
    }
    public double[][] data() {
        double[][] result = new double[this.shape.rows][this.shape.columns];
        for (int i = 0; i < this.shape.rows; i++) {
            for (int j = 0; j < this.shape.columns; j++) {
                result[i][j] = this.values[i];
            }
        }
        return result;
    }
    @Override
    public IDoubleMatrix times(double scalar){
        double[] result = new double[this.values.length];
        for(int i = 0; i < this.values.length; i++)
            result[i] = this.values[i] * scalar;
        return new Row(this.shape, result);
    }
    @Override
    public IDoubleMatrix plus(IDoubleMatrix m){
        assert(m.shape().rows == this.shape.rows && m.shape().columns == this.shape.columns);
        if(m.getClass() == Row.class){
            Row other;
            other = (Row) m;
            Shape newShape = new Shape(this.shape.rows, this.shape.columns);
            double[] result = new double[this.shape.rows];
            for(int i = 0; i < this.shape.rows; i++)result[i]=other.values[i]+this.values[i];
            return DoubleMatrixFactory.row(newShape, result);
        }
        else return super.plus(m);
    }
    @Override
    public double normInfinity(){
        double k = 0;
        for (double value : this.values) {
            k = Math.max(k, Math.abs(value));
        }
        return k * this.shape.columns;
    }
    @Override
    public double normOne(){
        double result = 0;
        for (double value : this.values) {
            result += Math.abs(value);
        }
        return result;
    }
    @Override
    public double frobeniusNorm(){
        return Math.sqrt(this.normInfinity() * this.shape.columns);
    }
    @Override
    public String toString(){
        if(this.shape.rows <= 2)return super.toString();
        StringBuilder result = new StringBuilder();
        result.append(this.rozmiarMacierzy());
        for(double value : this.values){
            result.append(value);
            result.append(" ... ");
            result.append(value);
            result.append('\n');
        }
        return result.toString();
    }
}
