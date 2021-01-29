package src.test;

import src.core.css.*;
import src.core.coreset.*;
import src.core.utils.*;

import java.util.*;
import java.io.*;

public class MainDiameter
{
    static DatasetReader reader;
    static double INF = 10e20;
    static int wSize = -1;
    static int stride = 10000;
    static int[] sizesw = new int[]{100, 1000, 3000, 10000, 30000, 100000, 300000};//, 1000000};
    public static void main(String[] args) throws Exception
    {

        PrintWriter writerCoh = new PrintWriter("out/test_diam_c_coh2.dat");
        PrintWriter writerCora = new PrintWriter("out/test_diam_c_cor2.dat");
        PrintWriter writerSeq = new PrintWriter("out/test_diam_c_seq2.dat");

        for(int ii=0; ii < sizesw.length; ii++){
            reader = new Cover_Reader("data/covtype.dat");
            wSize = sizesw[ii];
            long startTime, endTime;

            Diameter coh = new Diameter(0.001, wSize);
            CoresetDiameter cor = new CoresetDiameter(1, 1, wSize);
            ArrayList<Point> deb_win = new ArrayList<>();
            for(int tim = 0; tim < wSize+stride ; tim++){
                Point p = reader.nextPoint();
                if(tim%1000==0) System.out.println(tim);
                p.exitTime = tim + wSize;
                //debug window
                startTime = System.nanoTime();
                deb_win.add(p);
                endTime = System.nanoTime();
                if(tim >= wSize)
                writerSeq.print( (endTime - startTime) + " " );

                if(deb_win.size() > wSize)
                    deb_win.remove(0); //slow but it's for debug purposes only


                if(tim < wSize){ //only update
                    coh.update(p);
                    cor.update(p);
                    continue;
                }

                //benchmarks CSS update
                startTime = System.nanoTime();
                coh.update(p);
                endTime = System.nanoTime();
                writerCoh.print( (endTime - startTime) + " " );

                //benchmarks ours update
                startTime = System.nanoTime();
                cor.update(p);
                endTime = System.nanoTime();
                //writerCorc.print( (endTime - startTime) + " " );
                writerCora.print( (endTime - startTime) + " " );

                //benchmarks CSS q
                startTime = System.nanoTime();
                ArrayList<Point> centersCSS = coh.queryPoints();
                endTime = System.nanoTime();
                writerCoh.print( (endTime - startTime) + " " );

                //benchmarks ours q complete
                startTime = System.nanoTime();
                ArrayList<Point> centersCorAppr = cor.approxQuery();
                endTime = System.nanoTime();
                writerCora.print( (endTime - startTime) + " " );

                //benchmarks greedy q
                startTime = System.nanoTime();
                ArrayList<Point> centersSeq = CoresetDiameter.approxDiameter(deb_win);
                endTime = System.nanoTime();
                writerSeq.print( (endTime - startTime) + " " );

                //space of CSS
                int tmp = 0;
                tmp += coh.cold.size();
                tmp += coh.cnew.size();
                tmp += coh.q.size();
                tmp += coh.r.size();
                writerCoh.print(tmp + " ");
                //space of ours
                tmp = 0;
                for(Set<Point> tmpQ : cor.cor.OV.values())
                    tmp += tmpQ.size();
                for(Map<Point, Point> tmpM : cor.cor.RV.values())
                    tmp += tmpM.size()*2;
                for(Set<Point> tmpQ : cor.cor.O.values())
                    tmp += tmpQ.size();
                for(Map<Point, Point> tmpM : cor.cor.R.values())
                    tmp += tmpM.size()*2;
                writerCora.print(tmp + " ");

                writerSeq.print(deb_win.size() + " ");

                writerCoh.println(centersCSS.get(0).distance(centersCSS.get(1)));
                writerCora.println(centersCorAppr.get(0).distance(centersCorAppr.get(1)));
                writerSeq.println(centersSeq.get(0).distance(centersSeq.get(1))) ;
            }
            writerCoh.flush();
            writerSeq.flush();
            writerCora.flush();
            System.out.println("done "+ii);
        }
        writerCoh.close();
        writerSeq.close();
        writerCora.close();

        reader.close();
    }

    static ArrayList<Point> gonKCenter(ArrayList<Point> points, int k){
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
