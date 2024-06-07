package pl.edu.mimuw.matrix;

public class Full extends Matrix {
    private final double[][] data;
    public Full(double[][] data){
        assert (data != null);
        assert(data.length != 0);
        assert (data[0].length != 0);
        int pom = data[0].length;
        this.data = new double[data.length][pom];
        for(int i = 0; i < data.length; i++){
            assert (data[i].length == pom);
            System.arraycopy(data[i], 0, this.data[i], 0, data[i].length);
        }
        this.shape = new Shape(data.length, data[0].length);
    }
    @Override
    public double[][] data(){
        double[][] result = new double[this.data.length][this.data[0].length];
        for(int i = 0; i < this.data.length; i++){
            System.arraycopy(this.data[i], 0, result[i], 0, this.data[i].length);
        }
        return result;
    }
}
