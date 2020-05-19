package coreset;

import java.util.*;
import java.io.*;

public class Main
{
    static IO io = new IO("HIGGS.csv");
    static int k = 20;
    static double INF = 10e9;
    static int wSize = -1;
    static int stride = 1000;
    static int[] sizesw = new int[]{1000, 3000, 10000, 30000, 100000, 300000, 1000000};
    public static void main(String[] args) throws Exception
    {
        
    PrintWriter writerCoh = new PrintWriter("coh.dat");
    PrintWriter writerCor = new PrintWriter("cor.dat");
    PrintWriter writerSeq = new PrintWriter("seq.dat");
    
    for(int ii=0; ii < 4; ii++){
        wSize = sizesw[ii];
        long startTime, endTime;
        double avgCohr = 0, avgCorr = 0, avgSeqr = 0; //radius
        double avgCohs = 0, avgCors = 0, avgSeqs = 0; //space
        double avgCohq = 0, avgCorq = 0, avgSeqq = 0; //query time
        double avgCohu = 0, avgCoru = 0, avgSequ = 0; //update time
        
		Cohen coh = new Cohen(0.1, wSize, k);
		Coreset cor = new Coreset(0.1, 1, wSize, k);
		ArrayList<Point> deb_win = new ArrayList<>();
		for(int tim = 0; tim < wSize+stride ; tim++){
			for(int i=0; i<22; i++)
				io.getDouble(); //discard these
			double[] x = new double[7];
			for(int i=0; i<7; i++)
				x[i] = io.getDouble();// 7 high-level features
			Point p = new Point(x);
			
			
			//debug window
			deb_win.add(p);
			if(deb_win.size() > wSize)
				deb_win.remove(0); //slow but it's for debug purposes only
			//end debug
			
			
			if(tim < wSize){ //only update
                coh.update(p);
                cor.update(p);
				continue;
            }
                       
			//benchmarks cohen update
            startTime = System.nanoTime();
			coh.update(p);
            endTime = System.nanoTime();
            avgCohu += (endTime - startTime)/stride;
            
            //benchmarks ours update
            startTime = System.nanoTime();
			cor.update(p);
            endTime = System.nanoTime();
            avgCoru += (endTime - startTime)/stride;
            
            //benchmarks cohen q
            startTime = System.nanoTime();
			ArrayList<Point> centersCohen = coh.query();
            endTime = System.nanoTime();
            avgCohq += (endTime - startTime)/stride;
            
            //benchmarks ours q
            startTime = System.nanoTime();
			ArrayList<Point> centersCoreset = gonKCenter(cor.query());
            endTime = System.nanoTime();
            avgCorq += (endTime - startTime)/stride;
            
            //benchmarks greedy q
            startTime = System.nanoTime();
			ArrayList<Point> centersGreedy = gonKCenter(deb_win);
            endTime = System.nanoTime();
            avgSeqq += (endTime - startTime)/stride;
            
            //space of cohen
            int tmp = 0;
            for(Set<Point> tmpQ : coh.OV){
                tmp += tmpQ.size();
                //System.out.println("  "+tmpQ.toString());
            }
            //System.out.println(tmp+"\n");
            for(HashMap<Point, Point> tmpM : coh.RV)
                tmp += tmpM.size()*2;
            avgCohs += tmp/stride;
            
            //space of ours
            tmp = 0;
            for(Set<Point> tmpQ : cor.OV)
                tmp += tmpQ.size();
            for(Map<Point, Point> tmpM : cor.RV)
                tmp += tmpM.size()*2;
            for(Set<Point> tmpQ : cor.O)
                tmp += tmpQ.size();
            for(Map<Point, Point> tmpM : cor.R)
                tmp += tmpM.size()*2;
            avgCors += tmp/stride;
            
            
            //radius
			avgSeqr += distanceBetweenSets(deb_win, centersGreedy)/stride;
            avgCohr += distanceBetweenSets(deb_win, centersCohen)/stride;
            avgCorr += distanceBetweenSets(deb_win, gonKCenter(centersCoreset))/stride;
			
		}
        
        writerCoh.println(avgCohr+" "+avgCohs+" "+avgCohq+" "+avgCohu);
        writerCor.println(avgCorr+" "+avgCors+" "+avgCorq+" "+avgCoru);
        writerSeq.println(avgSeqr+" "+wSize+" "+avgSeqq+" "+avgSequ);
        
		System.out.println("done "+ii);
        }
    writerCoh.close();
    writerCor.close();
    writerSeq.close();
    
    io.close();
    }
	
	static ArrayList<Point> gonKCenter(ArrayList<Point> points){
		int n = points.size();
		ArrayList<Point> sol = new ArrayList<>();
		double[] distances = new double[n];
		Arrays.fill(distances, INF+1);
		int maxi = 0;
		for(int i=0; i < k; i++){
			sol.add(points.get(maxi));
			double max = 0;
			for(int j=0; j < n; j++){
				distances[j] = Math.min(distances[j], sol.get(i).distance(points.get(j)));
				if(distances[j] > max){
					max = distances[j];
					maxi = j;
				}
			}
		}
		return sol;
	}
	
	static double distanceBetweenSets(ArrayList<Point> set, ArrayList<Point> centers){
		double ans = 0;
		for(Point p : set){
			double dd = 100000000;
			for(Point q : centers){
				dd = Math.min(dd, p.distance(q));
			}
			ans = Math.max(dd, ans);
		}
		return ans;
	}
}



class IO extends PrintWriter {
	public IO() {
        super(new BufferedOutputStream(System.out));
        r = new BufferedReader(new InputStreamReader(System.in));
    }

    public IO(String fileName) {
        super(new BufferedOutputStream(System.out));
        try{
            r = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            this.println("File Not Found");
        }
    }

    public boolean hasMoreTokens() {
        return peekToken() != null;
    }

    public int getInt() {
        return Integer.parseInt(nextToken());
    }

    public double getDouble() {
        return Double.parseDouble(nextToken());
    }

    public long getLong() {
        return Long.parseLong(nextToken());
    }

    public String getWord() {
        return nextToken();
    }

	public String getLine(){
        try{
            st = null;
            return r.readLine();
        }
        catch(IOException ex){}
        return null;
    }
	

    private BufferedReader r;
    private String line;
    private StringTokenizer st;
    private String token;

    private String peekToken() {
        if (token == null)
            try {
                while (st == null || !st.hasMoreTokens()) {
                    line = r.readLine();
                    if (line == null) return null;
                    st = new StringTokenizer(line, " \t\n\r\f,;");
                }
                token = st.nextToken();
            } catch (IOException e) { }
        return token;
    }

    private String nextToken() {
        String ans = peekToken();
        token = null;
        return ans;
    }
}


class Point
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
		//return Arrays.hashCode(x);
	}
	
	public String toString()
	{
            return "("+exitTime+")";
            //return Arrays.toString(x);
	}
	
	double[] x;
	int exitTime;
}
