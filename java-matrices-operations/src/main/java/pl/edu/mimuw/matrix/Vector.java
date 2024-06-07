package pl.edu.mimuw.matrix;

public class Vector extends Matrix{
    private final double[] values;
    public Vector(double... v){
        assert(v != null);
        assert(v.length != 0);
        this.values = new double[v.length];
        System.arraycopy(v, 0, this.values, 0, v.length);
        this.shape = new Shape(v.length, 1);
        //this.Shape.assertInShape(this.Shape.rows,this.Shape.columns);
    }
    public double[][] data(){
        double[][] result = new double[this.values.length][1];
        for(int i = 0; i < values.length; i++)result[i][0] = values[i];
        return result;
    }
}
