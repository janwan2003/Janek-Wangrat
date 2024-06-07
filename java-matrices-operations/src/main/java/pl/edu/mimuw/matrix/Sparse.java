package pl.edu.mimuw.matrix;


import java.util.Arrays;
public class Sparse extends Matrix {
    private final MatrixCellValue[] values;
    public Sparse(Shape s, MatrixCellValue... v){
        MatrixCellValue[] beforesort= new MatrixCellValue[v.length];
        for(int i = 0; i < v.length; i++)beforesort[i] = MatrixCellValue.cell(v[i].row,v[i].column,v[i].value);
        Arrays.sort(beforesort);
        this.values = beforesort;
        this.shape = pl.edu.mimuw.matrix.Shape.matrix(s.rows,s.columns);
        for (MatrixCellValue matrixCellValue : v) {
            this.shape.assertInShape(matrixCellValue.row, matrixCellValue.column);
        }
    }

    public double[][] data(){
        double[][] result = (new Zero(pl.edu.mimuw.matrix.Shape.matrix(this.shape.rows, this.shape.columns))).data() ;
        for (MatrixCellValue value : values) {
            result[value.row][value.column] = value.value;
        }
        return result;
    }

    @Override
    public IDoubleMatrix times (IDoubleMatrix m){
        assert(this.shape.columns == m.shape().rows);
        if(m.getClass() == Sparse.class){
            Sparse other;
            other = (Sparse) m;
            MatrixCellValue[] result = new MatrixCellValue[this.values.length * other.values.length];
            int idx = 0;
            for (MatrixCellValue value : this.values) {
                int x = 0;
                int y = other.values.length - 1;
                while (x != y) {
                    int h = (x + y) / 2;
                    if (other.values[h].row >= value.column) {
                        y = h;
                    } else x = h + 1;
                }
                if (x != 0) while (x > 0 && other.values[x - 1].row == value.column) x--;

                while (other.values[x].row == value.column){
                    int a = value.row;
                    int b = other.values[x].column;
                    double c = value.value * other.values[x].value;
                    MatrixCellValue ce = MatrixCellValue.cell(a, b, c);
                    result[idx] = ce;
                    idx++;
                    x++;
                    if(x == other.values.length)break;
                }
            }
            int ile_usunieto=0;
            for(int i = 0; i < idx; i++){
                for(int j = i + 1; j < idx; j++){
                    if(result[i].row == result[j].row && result[i].column == result[j].column){
                        result[j] = MatrixCellValue.cell(result[j].row,result[j].column,result[j].value+result[i].value);
                        result[i] = null;
                        ile_usunieto++;
                        break;
                    }
                }
            }
            MatrixCellValue[] shortresult = new MatrixCellValue[idx-ile_usunieto];
            int idx2=0;
            for(int i = 0; i < idx; i++) {
                if(result[i] != null) {
                    shortresult[idx2] = result[i];
                    idx2++;
                }
            }
            return new Sparse(pl.edu.mimuw.matrix.Shape.matrix(this.shape.rows, other.shape().columns), shortresult);
        }
        else{
            return super.times(m);
        }
    }
    @Override
    public IDoubleMatrix times (double m){
        MatrixCellValue[] result = new MatrixCellValue[this.values.length];
        Shape s = pl.edu.mimuw.matrix.Shape.matrix(this.shape.rows, this.shape.columns);
        for(int i = 0; i < this.values.length; i++){
            result[i] = MatrixCellValue.cell(this.values[i].row, this.values[i].column, this.values[i].value * m);
        }
        return new Sparse(s, result);
    }

    @Override
    public IDoubleMatrix plus(IDoubleMatrix m){
        assert(this.shape.rows == m.shape().rows && this.shape.columns == m.shape().columns);

        if(m.getClass() == Sparse.class) {
            Sparse other;
            other = (Sparse) m;
            MatrixCellValue[] result = new MatrixCellValue[this.values.length + other.values.length];
            for (int i = 0; i < this.values.length; i++) {
                result[i] = MatrixCellValue.cell(this.values[i].row, this.values[i].column, this.values[i].value);
            }
            for (int i = this.values.length; i < result.length; i++) {
                result[i] = MatrixCellValue.cell(other.values[i - this.values.length].row, other.values[i - this.values.length].column, other.values[i - this.values.length].value);
            }
            int ile_usunieto = 0;
            for (int i = 0; i < result.length; i++) {
                for (int j = i + 1; j < result.length; j++) {
                    if (result[i].row == result[j].row && result[i].column == result[j].column) {
                        result[j] = MatrixCellValue.cell(result[j].row, result[j].column, result[j].value + result[i].value);
                        result[i] = null;
                        ile_usunieto++;
                        break;
                    }
                }
            }
            MatrixCellValue[] shortresult = new MatrixCellValue[result.length - ile_usunieto];
            int idx2 = 0;
            for (MatrixCellValue matrixCellValue : result) {
                if (matrixCellValue != null) {
                    shortresult[idx2] = matrixCellValue;
                    idx2++;
                }
            }
            return new Sparse(Shape.matrix(this.shape.rows, this.shape.columns), shortresult);
        }
        else{
            return super.plus(m);
        }
    }
    @Override
    public IDoubleMatrix minus (IDoubleMatrix m){
        return plus(m.times(-1));
    }
    @Override
    public double get (int row, int column){
        this.shape.assertInShape(row, column);
        for (MatrixCellValue value : this.values) {
            if (value.row == row && value.column == column) return value.value;
        }
        return 0;
    }
    @Override
    public double normOne(){
        double[] tab = new double[this.shape.columns];
        for (MatrixCellValue value : this.values) {
            tab[value.column] += Math.abs(value.value);
        }
        double result = 0;
        for (double v : tab) {
            result = Math.max(result, v);
        }
        return result;
    }
    @Override
    public double normInfinity(){
        double[] tab = new double[this.shape.rows];
        for (MatrixCellValue value : this.values) {
            tab[value.row] += Math.abs(value.value);
        }
        double result = 0;
        for (double v : tab) {
            result = Math.max(result, v);
        }
        return result;
    }
    @Override
    public double frobeniusNorm(){
       double result = 0;
        for (MatrixCellValue value : this.values) {
            result += value.value * value.value;
        }
       return Math.sqrt(result);
    }
    //uznałem, że dla macierzy typu sparse, która często potrafi być bardzo duża łatwiej będzie nam dostawać wartości w komórkach niż całą macierz
    @Override
    public String toString(){
        StringBuilder result = new StringBuilder();
        result.append(this.rozmiarMacierzy());
        for (MatrixCellValue value : this.values) {
            result.append(value.toString());
            result.append("\n");
        }
        return result.toString();
    }
}
