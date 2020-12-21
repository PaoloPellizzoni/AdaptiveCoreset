package src.core.utils;

public class Point
{
    public Point(double[] _x)
    {
        x = _x;
    }
    
    public double distance(Point p)
    {
        double tmp = 0;
        for(int i=0; i<x.length; i++)
            tmp += (x[i] - p.x[i])*(x[i] - p.x[i]);
        return Math.sqrt(tmp);
    }
    
    public int hashCode()
    {
        return exitTime;
    }
    
    public String toString()
    {
            return "("+exitTime+")";
    }
	
	public boolean equals(Object o){
		if(!(o instanceof Point)) return false;
		return this.exitTime == ((Point)o).exitTime;
	}
    
    double[] x;
    public int exitTime;
}
