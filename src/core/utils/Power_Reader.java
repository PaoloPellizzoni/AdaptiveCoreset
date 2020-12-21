package src.core.utils;

public class Power_Reader implements DatasetReader
{
    public Power_Reader(String file){
        io = new IO(file);
        io.getLine();
    }
    
    public void close(){
        io.close();
    }
    
    public Point nextPoint(){
        double[] x = new double[7];
        io.getWord(); io.getWord(); //discard
        for(int i=0; i<7; i++){
            x[i] = io.getDouble();// 7 numeric features
        }
        return new Point(x);
    }
    
    private IO io;
}