package src.test;

import src.core.css.*;
import src.core.coreset.*;
import src.core.utils.*;

import java.util.*;
import java.io.*;

public class MainDD
{
    static DatasetReader reader;
    static int k = 20;
    static double INF = 10e20;
    static int wSize = 10000;
    public static void main(String[] args) throws Exception
    {
        PrintWriter writerCorOb = new PrintWriter("test_alpha_ob.dat");
        PrintWriter writerCor = new PrintWriter("test_alpha.dat");
        CoresetOblivious corOb = new CoresetOblivious(0.1, 1, wSize, k);
        Coreset cor = new Coreset(0.1, 1, wSize, k, 0.001, 500);
        reader = new DD_Reader("data/alpha.dat");
        for(int tim = 0; tim < 90000 ; tim++){
            if(tim%1000==0) System.out.println(tim);
            Point p = reader.nextPoint();
            p.exitTime = tim + wSize;
            cor.update(p);
            corOb.update(p);

            // space
            int tmp = 0;
            for(Set<Point> tmpQ : cor.OV)
                tmp += tmpQ.size();
            for(Map<Point, Point> tmpM : cor.RV)
                tmp += tmpM.size()*2;
            for(Set<Point> tmpQ : cor.O)
                tmp += tmpQ.size();
            for(Map<Point, Point> tmpM : cor.R)
                tmp += tmpM.size()*2;

            writerCor.println(tmp + " " + cor.minDist + " " + cor.maxDist);

            // space
            tmp = 0;
            for(Set<Point> tmpQ : corOb.OV.values())
                tmp += tmpQ.size();
            for(Map<Point, Point> tmpM : corOb.RV.values())
                tmp += tmpM.size()*2;
            for(Set<Point> tmpQ : corOb.O.values())
                tmp += tmpQ.size();
            for(Map<Point, Point> tmpM : corOb.R.values())
                tmp += tmpM.size()*2;

            writerCorOb.println(tmp+ " " + corOb.r_t/2 + " " + corOb.M_t*2/corOb.delta + " " + corOb.OV.size());


        }
        writerCor.close();
        writerCorOb.close();
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
