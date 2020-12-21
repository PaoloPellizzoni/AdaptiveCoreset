package src.core.utils;

public class Cover_Reader implements DatasetReader
{
    public Cover_Reader(String file){
        io = new IO(file);
    }

    public void close(){
        io.close();
    }

    public Point nextPoint(){
        double[] x = new double[54];
        for(int i=0; i<54; i++){
            x[i] = io.getDouble();
        }
        return new Point(x);
    }

    private IO io;
}
