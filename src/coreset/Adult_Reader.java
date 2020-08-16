public class Adult_Reader implements DatasetReader
{
    public Adult_Reader(String file){
        io = new IO(file);
    }
    
    public void close(){
        io.close();
    }
    
    public Point nextPoint(){
        double[] x = new double[4];
        String s = io.getWord(); // age
        x[0] = Double.parseDouble(s);
        io.getWord(); io.getWord(); io.getWord(); //discard
        s = io.getWord(); // education
        x[1] = Double.parseDouble(s);
        io.getWord(); io.getWord(); io.getWord(); io.getWord(); io.getWord(); //discard
        s = io.getWord(); // capital gain
        x[2] = Double.parseDouble(s);
        io.getWord(); // discard
        s = io.getWord(); // hours per week
        x[3] = Double.parseDouble(s);
        io.getWord(); io.getWord();
        return new Point(x);
    }
    
    private IO io;
}