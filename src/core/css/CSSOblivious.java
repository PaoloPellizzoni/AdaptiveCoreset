package src.core.css;

import src.core.utils.*;
import src.core.css.Diameter;
import java.util.*;

public class CSSOblivious
{
    public CSSOblivious(double _eps, int _wSize, int _k)
    {
        eps = _eps;
        wSize = _wSize;
        k = _k;
        RV = new TreeMap<>();
        OV = new TreeMap<>();
        last_points = new TreeSet<>((aa, bb)-> aa.exitTime-bb.exitTime);
    }

    public ArrayList<Point> query()
    {
        int i = RV.firstKey();
        while(i <= RV.lastKey())
        {
            double gamma = Math.pow(1+eps, i);
            ArrayList<Point> C = new ArrayList<>();
            for(Point p : RV.get(i).keySet())
            {
                C.add(p);
            }
            for(Point p : OV.get(i))
            {
                if(C.size() > k)
                    break;
                double mind = Double.POSITIVE_INFINITY;
                for(Point q : C)
                    mind = Math.min(mind, p.distance(q));
                if(mind > 2*gamma)
                    C.add(p);
            }
            if(C.size()<=k)
                return C;

            i++;
        }
        return null;
    }

    public void update(Point p)
    {
        // update r
        if(first_point == null){
            first_point = p;
            last_points.add(p);
            step++;
            return;
        }
        Point old_last = last_points.first();
        Point old_new = last_points.last();
        if(last_points.size() > k){
            last_points.remove(old_last);
        }
        last_points.add(p);
        r_t = minPairwiseDistance(last_points)+1e-9;
        // end updating


        // update sets
        ArrayList<Integer> gams = new ArrayList<>();
        for(Integer gam : RV.keySet())
            gams.add(gam);
        for(Integer gam : gams){
            if(gam < Math.floor(Math.log(r_t/2)/Math.log(1+eps)) ){
                RV.remove(gam);
                OV.remove(gam);
            }
        }
        if(!RV.isEmpty()){
            int low = RV.firstKey() - 1;
            while(low >= Math.floor(Math.log(r_t/2)/Math.log(1+eps)) ){
                TreeMap<Point, Point> RVi = new TreeMap<>((aa, bb)-> aa.exitTime-bb.exitTime);
                RVi.put(old_last, old_last);
                for(Point pp : last_points){
                    if(pp != p)
                        RVi.put(pp, pp);
                }
                RV.put(low, RVi);
                OV.put(low, new TreeSet<>((aa, bb)-> aa.exitTime-bb.exitTime));

                low--;
            }
        }
        // end updating

        // add p for each gamma
        int i = (int)Math.floor(Math.log(r_t)/Math.log(1+eps));
        M_t = Double.POSITIVE_INFINITY;
        while(true){
            double gamma = Math.pow(1+eps, i);
            // reached upper bound, cleanup and exit
            if(gamma > M_t){
                for(int j = i; j <= RV.lastKey(); j++){
                    RV.remove(j);
                    OV.remove(j);
                }
                break;
            }
            if(RV.isEmpty() || i > RV.lastKey()){
                TreeMap<Point, Point> RVi = new TreeMap<>((aa, bb)-> aa.exitTime-bb.exitTime);
                RVi.put(old_new, old_new);
                RV.put(i, RVi);

                OV.put(i, new TreeSet<>((aa, bb)-> aa.exitTime-bb.exitTime));
            }


            // REMOVE OLD POINTS
            ArrayList<Point> ptsToDel = new ArrayList<>();

            if(!RV.get(i).isEmpty() && RV.get(i).firstKey().exitTime <= step){
                OV.get(i).add(RV.get(i).get(RV.get(i).firstKey()));
                RV.get(i).remove(RV.get(i).firstKey());
            }
            while(!OV.get(i).isEmpty() && OV.get(i).first().exitTime <= step)
                OV.get(i).remove(OV.get(i).first());

            // INSERT NEW POINT p
            ArrayList<Point> E = new ArrayList<>(); // within radius of a validation pt
            for(Point q : RV.get(i).keySet()){
                if( p.distance(q) < gamma*2 ){
                    E.add(q);
                }
            }
            if(E.isEmpty()){
                RV.get(i).put(p, p);
                if(RV.get(i).size() > k+1){ // keep size <= k+1
                    Point vOld = RV.get(i).firstKey();
                    OV.get(i).add(RV.get(i).get(vOld));
                    RV.get(i).remove(vOld);

                }
                if(RV.get(i).size() > k){ // surely can't find a k cluster, so delete
                    int tOld = RV.get(i).firstKey().exitTime;

                    while(!OV.get(i).isEmpty() && OV.get(i).first().exitTime <= tOld)
                        OV.get(i).remove(OV.get(i).first());
                 }
            } else {
                for(Point a : E){
                    //RV.get(i).remove(a);
                    RV.get(i).put(a, p);
                }
            }

            // ENDED INSERTING

            if(M_t == Double.POSITIVE_INFINITY){
                ArrayList<Point> C = new ArrayList<>();
                for(Point pp : RV.get(i).keySet())
                {
                    C.add(pp);
                }
                for(Point pp : OV.get(i))
                {
                    if(C.size() > k)
                        break;
                    double mind = Double.POSITIVE_INFINITY;
                    for(Point q : C)
                        mind = Math.min(mind, pp.distance(q));
                    if(mind > 2*gamma)
                        C.add(pp);
                }
                if(C.size()<=k)
                    M_t = 12*gamma;
            }

            i++;
        }
        step++; //next step
    }

    private double minPairwiseDistance(Iterable<Point> points){
        double ans = Double.POSITIVE_INFINITY;
        for(Point p1 : points){
            for(Point p2 : points){
                if(p1 != p2)
                    ans = Math.min(ans, p1.distance(p2));
            }
        }
        return ans;
    }

    public TreeMap<Integer, TreeMap<Point, Point>> RV;
    public TreeMap<Integer, TreeSet<Point>> OV;
    TreeSet<Point> last_points;
    Point first_point;
    public double r_t, M_t;
    int step = 0, wSize, k;
    public double eps;
}
