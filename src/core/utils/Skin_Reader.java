package src.core.utils;

public class Skin_Reader implements DatasetReader
{
    public Skin_Reader(String file){
        io = new IO(file);
    }

    public void close(){
        io.close();
    }

    public Point nextPoint(){
        double[] x = new double[4];
        x[0] = io.getDouble();
        x[1] = io.getDouble();
        x[2] = io.getDouble();
        x[3] = io.getDouble();
        return new Point(x);
    }

    private IO io;
}
