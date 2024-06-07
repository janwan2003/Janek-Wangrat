package pl.edu.mimuw.matrix;

public class AntiDiagonal extends Diagonal{
        public AntiDiagonal(double... v){
            super(v);
        }
        public AntiDiagonal(double vo, double... v){super(vo, v);}

       public double[][] data(){
           double[][] result = (new Zero(new Shape(this.shape.rows, this.shape.columns))).data() ;
           for(int i = 0; i < this.values.length; i++){
                result[this.shape.rows - 1 - i][i] = this.values[i];
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
                   result[i][j] = A[values.length - i - 1][j] * this.values[values.length - i - 1];
               }
           }
           return new Full(result);
        }
    @Override
    public IDoubleMatrix plus(IDoubleMatrix m){
        assert(m.shape().rows == this.shape.rows && m.shape().columns == this.shape.columns);
        if(m.getClass() == AntiDiagonal.class){
            AntiDiagonal other;
            other = (AntiDiagonal) m;
            double[] result = new double[this.shape.rows];
            for(int i = 0; i < this.shape.rows; i++)result[i] = other.values[i] + this.values[i];
            return new AntiDiagonal(result);
        }
        else if (m.getClass() == Constant.class) {
            Constant other;
            other = (Constant) m;
            double[] result = new double[this.shape.rows];
            for(int i = 0; i < this.shape.rows; i++)result[i] = other.giveValue() + this.values[i];
            return new AntiDiagonal(this.othervalues + other.giveValue(), result);
        }
        else return super.plus(m);
    }

    @Override
    public IDoubleMatrix plus(double scalar){
        double[] result = new double[this.shape.rows];
        for(int i = 0; i < this.shape.rows; i++)result[i] = scalar + this.values[i];
        return new AntiDiagonal(scalar + this.othervalues, result);
    }
    @Override
    public String toString(){
        if(this.shape.rows <= 3)return super.toString();
        StringBuilder result = new StringBuilder();
        result.append(this.rozmiarMacierzy());
        for(int i = 0; i < this.shape.rows; i++){
            for(int j = 0; j < this.shape.columns; j++){
                if(Math.abs(i+j-this.shape.columns + 1)<=1 || i == 0 || i == this.shape.columns-1 || j ==0 || j==this.shape.columns-1)
                    result.append(this.data()[i][j]).append(" ");
                else if(Math.abs(i + j - this.shape.columns + 1) == 2)
                    result.append("... ");
                else result.append("    ");
            }
            result.append("\n");
        }
        return result.toString();
    }
}
