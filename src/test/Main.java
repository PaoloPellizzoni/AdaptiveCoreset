package src.test;

import src.core.css.*;
import src.core.coreset.*;
import src.core.utils.*;	

import java.util.*;
import java.io.*;

public class Main
{
    static DatasetReader reader;
    static int k = 20;
    static double INF = 10e20;
    static int wSize = -1;
    static int stride = 10000;
    static int[] sizesw = new int[]{100, 1000, 3000, 10000, 30000, 100000, 300000};//, 1000000};
    public static void main(String[] args) throws Exception
    {

        PrintWriter writerCoh = new PrintWriter("test_cover_coh.dat");
        PrintWriter writerCor = new PrintWriter("test_cover_cor.dat");
        PrintWriter writerSeq = new PrintWriter("test_cover_seq.dat");
        PrintWriter writerObl = new PrintWriter("test_cover_obl.dat");

        for(int ii=0; ii < sizesw.length; ii++){
            reader = new Cover_Reader("data/covtype.dat");
            wSize = sizesw[ii];
            long startTime, endTime;

            CSS coh = new CSS(0.01, wSize, k, 0.1, 20000);
            Coreset cor = new Coreset(0.1, 0.5, wSize, k, 0.1, 20000);
            CoresetOblivious obl = new CoresetOblivious(0.1, 0.5, wSize, k);
            ArrayList<Point> deb_win = new ArrayList<>();
            for(int tim = 0; tim < wSize+stride ; tim++){
                Point p = reader.nextPoint();
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
                    obl.update(p);
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
                writerCor.print( (endTime - startTime) + " " );

                //benchmarks obl update
                startTime = System.nanoTime();
                obl.update(p);
                endTime = System.nanoTime();
                writerObl.print( (endTime - startTime) + " " );

                //benchmarks CSS q
                startTime = System.nanoTime();
                ArrayList<Point> centersCSS = coh.query();
                endTime = System.nanoTime();
                writerCoh.print( (endTime - startTime) + " " );

                //benchmarks ours q
                startTime = System.nanoTime();
                ArrayList<Point> centersCoreset = gonKCenter(cor.query());
                endTime = System.nanoTime();
                writerCor.print( (endTime - startTime) + " " );

                //benchmarks ours q
                startTime = System.nanoTime();
                ArrayList<Point> centersCoresetOb = gonKCenter(obl.query());
                endTime = System.nanoTime();
                writerObl.print( (endTime - startTime) + " " );


                //benchmarks greedy q
                startTime = System.nanoTime();
                ArrayList<Point> centersGreedy = gonKCenter(deb_win);
                endTime = System.nanoTime();
                writerSeq.print( (endTime - startTime) + " " );

                //space of CSS
                int tmp = 0;
                for(Set<Point> tmpQ : coh.OV){
                    tmp += tmpQ.size();
                }
                for(HashMap<Point, Point> tmpM : coh.RV)
                    tmp += tmpM.size()*2;
                writerCoh.print(tmp + " ");

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
                writerCor.print(tmp + " ");

                //space of obl
                tmp = 0;
                for(Set<Point> tmpQ : obl.OV.values())
                    tmp += tmpQ.size();
                for(Map<Point, Point> tmpM : obl.RV.values())
                    tmp += tmpM.size()*2;
                for(Set<Point> tmpQ : obl.O.values())
                    tmp += tmpQ.size();
                for(Map<Point, Point> tmpM : obl.R.values())
                    tmp += tmpM.size()*2;
                writerObl.print(tmp + " ");

                writerSeq.print(deb_win.size() + " ");

                writerCoh.println(distanceBetweenSets(deb_win, centersCSS));
                writerCor.println(distanceBetweenSets(deb_win, centersCoreset));
                writerObl.println(distanceBetweenSets(deb_win, centersCoresetOb));
                writerSeq.println(distanceBetweenSets(deb_win, centersGreedy)) ;
            }
            writerCoh.flush();
            writerCor.flush();
            writerSeq.flush();
            writerObl.flush();
            System.out.println("done "+ii);
        }
        writerCoh.close();
        writerCor.close();
        writerSeq.close();
        writerObl.close();

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
