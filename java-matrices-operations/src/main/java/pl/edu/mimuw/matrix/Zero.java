package pl.edu.mimuw.matrix;

public class Zero extends Constant {
    public Zero(Shape s){
        super(s,0);
    }
    @Override
    public IDoubleMatrix times (IDoubleMatrix other){
        assert(this.shape.columns == other.shape().rows);
        return this;
    }
    @Override
    public double normOne(){
        return 0;
    }
    @Override
    public IDoubleMatrix plus(IDoubleMatrix other){
        return other;
    }
    @Override
    public IDoubleMatrix minus(IDoubleMatrix other) {return other;}
    @Override
    public double normInfinity(){
        return 0;
    }
    @Override
    public double frobeniusNorm(){
        return 0;
    }
}
