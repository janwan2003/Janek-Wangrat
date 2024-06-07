package pl.edu.mimuw.matrix;

public class Column extends Matrix{
    private final double[] values;
    public Column(Shape s,double[] v){
        assert(s.columns == v.length);
        this.values = new double[v.length];
        System.arraycopy(v, 0, this.values, 0, v.length);
        this.shape = pl.edu.mimuw.matrix.Shape.matrix(s.rows,s.columns);
    }
    public double[][] data() {
        double[][] result = new double[this.shape.rows][this.shape.columns];
        for (int i = 0; i < this.shape.columns; i++) {
            for (int j = 0; j < this.shape.rows; j++) {
                result[j][i] = this.values[i];
            }
        }
        return result;
    }
    @Override
    public IDoubleMatrix times(double scalar){
        double[] result = new double[this.values.length];
        for(int i = 0; i < this.values.length; i++)
            result[i] = this.values[i] * scalar;
        return new Column(this.shape, result);
    }
    @Override
    public IDoubleMatrix plus(IDoubleMatrix m){
        assert(m.shape().rows == this.shape.rows && m.shape().columns == this.shape.columns);
        if(m.getClass() == Column.class){
            Column other;
            other = (Column) m;
            Shape newShape = new Shape(this.shape.rows, this.shape.columns);
            double[] result = new double[this.shape.columns];
            for(int i = 0; i < this.shape.columns; i++)result[i]=other.values[i]+this.values[i];
            return DoubleMatrixFactory.column(newShape, result);
        }
        else return super.plus(m);
    }
    @Override
    public double normOne(){
        double k = 0;
        for (double value : this.values) {
            k = Math.max(k, Math.abs(value));
        }
        return k * this.shape.columns;
    }
    @Override
    public double normInfinity(){
        double result = 0;
        for (double value : this.values) {
            result += Math.abs(value);
        }
        return result;
    }
    @Override
    public double frobeniusNorm(){
        return Math.sqrt(this.normOne() * this.shape.columns);
    }

    @Override
    public String toString(){
        if(this.shape.rows <= 2)return super.toString();
        StringBuilder result = new StringBuilder();
        result.append(this.rozmiarMacierzy());
        for (double value : this.values) {
            result.append(value);
            result.append(" ");
        }
        result.append('\n');
        result.append("... ".repeat(this.values.length));
        result.append('\n');
        for (double value : this.values) {
            result.append(value);
            result.append(" ");
        }
        result.append('\n');
        return result.toString();
    }
}
