package src.core.utils;

public class DD_Reader100 implements DatasetReader
{
    public DD_Reader100(String file){
        io = new IO(file);
    }

    public void close(){
        io.close();
    }

    public Point nextPoint(){
        double[] x = new double[100];
        for(int i=0; i<100; i++)
            x[i] = io.getDouble();
        return new Point(x);
    }

    private IO io;
}
