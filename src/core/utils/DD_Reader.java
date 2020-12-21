package src.core.utils;

public class DD_Reader implements DatasetReader
{
    public DD_Reader(String file){
        io = new IO(file);
    }

    public void close(){
        io.close();
    }

    public Point nextPoint(){
        double[] x = new double[10];
        for(int i=0; i<10; i++)
            x[i] = io.getDouble();
        return new Point(x);
    }

    private IO io;
}
