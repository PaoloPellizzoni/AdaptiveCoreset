public class HIGGS_Reader implements DatasetReader
{
    public HIGGS_Reader(String file){
        io = new IO(file);
    }
    
    public void close(){
        io.close();
    }
    
    public Point nextPoint(){
        for(int i=0; i<22; i++)
            io.getDouble(); //discard these
        double[] x = new double[7];
        for(int i=0; i<7; i++)
            x[i] = io.getDouble();// 7 high-level features
        return new Point(x);
    }
    
    private IO io;
}