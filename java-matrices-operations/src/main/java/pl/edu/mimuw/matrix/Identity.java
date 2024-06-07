package pl.edu.mimuw.matrix;

public class Identity extends Diagonal{
    public Identity(int s){
        super((new Constant(new Shape(1, s), 1)).data()[0]);
    }
    @Override
    public IDoubleMatrix times (IDoubleMatrix other){
        return other;
    }
    @Override
    public double normOne(){
        return 1;
    }
    @Override
    public double normInfinity(){
        return 1;
    }
    @Override
    public double frobeniusNorm(){
        return Math.sqrt(this.shape.rows);
    }
}
