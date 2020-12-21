package src.test;

import src.core.parallel.*;
import src.core.utils.*;

import java.util.*;
import java.io.*;

public class MainParallel
{
    static DatasetReader reader;
    static int k = 20;
    static double INF = 10e20;
    static int wSize = -1;
    static int stride = 1000;
    static int[] sizesw = new int[]{1000, 100000};
    public static void main(String[] args) throws Exception
    {
        System.out.println(Runtime.getRuntime().availableProcessors());

        PrintWriter writerCor = new PrintWriter("parallel.dat");

        for(int ii=0; ii < 2; ii++){
            reader = new Power_Reader("data/power.dat");
            wSize = sizesw[ii];
            long startTime, endTime;
            double avgCorr = 0; //radius
            double avgCors = 0; //space
            double avgCorq = 0; //query time
            double avgCoru = 0; //update time


            ParallelCoreset cor = new ParallelCoreset(0.01, 1, wSize, k, 0.001, 1000, 1);
            ArrayList<Point> deb_win = new ArrayList<>();
            for(int tim = 0; tim < wSize+stride ; tim++){
                Point p = reader.nextPoint();
                //debug window
                deb_win.add(p);
                if(deb_win.size() > wSize)
                    deb_win.remove(0); //slow but it's for debug purposes only

                if(tim < wSize){ //only update
                    cor.update(p);
                    continue;
                }

                //benchmarks ours update
                startTime = System.nanoTime();
                cor.update(p);
                endTime = System.nanoTime();
                avgCoru += (endTime - startTime)/stride;

                //benchmarks ours q
                startTime = System.nanoTime();
                ArrayList<Point> centersCoreset = gonKCenter(cor.query());
                endTime = System.nanoTime();
                avgCorq += (endTime - startTime)/stride;




                //radius
                avgCorr += distanceBetweenSets(deb_win, centersCoreset)/stride;
            }

            writerCor.println(avgCorr+" "+avgCors+" "+avgCorq+" "+avgCoru);
            cor.shutdownWorkers();
            System.out.println("done "+ii);
        }
        writerCor.close();

        reader.close();
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
            double dd = INF+1;
            for(Point q : centers){
                dd = Math.min(dd, p.distance(q));
            }
            ans = Math.max(dd, ans);
        }
        return ans;
    }
}
