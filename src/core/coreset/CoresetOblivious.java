package src.core.coreset;

import src.core.utils.*;
import src.core.css.Diameter;
import java.util.*;

public class CoresetOblivious
{
    public CoresetOblivious(double _beta, double _eps, int _wSize, int _k)
    {
        beta = _beta;
        delta = _eps/(1+beta);
        wSize = _wSize;
        k = _k;
        RV = new TreeMap<>();
        OV = new TreeMap<>();
        R = new TreeMap<>();
        O = new TreeMap<>();
        last_points = new TreeSet<>((aa, bb)-> aa.exitTime-bb.exitTime);
        diameter = new Diameter(beta, wSize);
    }

    public ArrayList<Point> query()
    {
        int i = RV.firstKey();
        while(i <= RV.lastKey())
        {
            double gamma = Math.pow(1+beta, i);
            ArrayList<Point> C = new ArrayList<>();
            for(Point p : RV.get(i).keySet())
            {
                C.add(p);
            }
            for(Point p : OV.get(i))
            {
                if(C.size() > k)
                    break;
                double mind = M_t*8+1;
                for(Point q : C)
                    mind = Math.min(mind, p.distance(q));
                if(mind > 2*gamma)
                    C.add(p);
            }
            for(Point p : RV.get(i).values())
            {
                if(C.size() > k)
                    break;
                double mind = M_t*8+1;
                for(Point q : C)
                    mind = Math.min(mind, p.distance(q));
                if(mind > 2*gamma)
                    C.add(p);
            }
            if(C.size()<=k)
                break;

            i++;
        }
        ArrayList<Point> core = new ArrayList<>();
        for(Point p : R.get(i).values())
            core.add(p);
        for(Point p : O.get(i))
            core.add(p);
        return core;
    }

    public void update(Point p)
    {
        diameter.update(p);
        // update r and M
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
        M_t = 3*(1+beta)*diameter.query();
        // end updating


        // update sets
        ArrayList<Integer> gams = new ArrayList<>();
        for(Integer gam : RV.keySet())
            gams.add(gam);
        for(Integer gam : gams){
            if(gam < Math.floor(Math.log(r_t/2)/Math.log(1+beta)) ||  gam >Math.ceil(Math.log(2*M_t/delta)/Math.log(1+beta)) ){
                RV.remove(gam);
                R.remove(gam);
                OV.remove(gam);
                O.remove(gam);
            }
        }
        if(!RV.isEmpty()){
            int low = RV.firstKey() - 1;
            int high = RV.lastKey() + 1;
            while(low >= Math.floor(Math.log(r_t/2)/Math.log(1+beta)) ){
                TreeMap<Point, Point> RVi = new TreeMap<>((aa, bb)-> aa.exitTime-bb.exitTime);
                RVi.put(old_last, old_last);
                for(Point pp : last_points){
                    if(pp != p)
                        RVi.put(pp, pp);
                }
                RV.put(low, RVi);

                TreeMap<Point, Point> Ri = new TreeMap<>((aa, bb)-> aa.exitTime-bb.exitTime);
                Ri.put(old_last, old_last);
                for(Point pp : last_points){
                    if(pp != p)
                        Ri.put(pp, pp);
                }
                R.put(low, Ri);

                OV.put(low, new TreeSet<>((aa, bb)-> aa.exitTime-bb.exitTime));
                O.put(low, new TreeSet<>((aa, bb)-> aa.exitTime-bb.exitTime));

                low--;
            }

            while(high <= Math.ceil(Math.log(2*M_t/delta)/Math.log(1+beta))){
                TreeMap<Point, Point> RVi = new TreeMap<>((aa, bb)-> aa.exitTime-bb.exitTime);
                RVi.put(old_new, old_new);
                RV.put(high, RVi);

                TreeMap<Point, Point> Ri = new TreeMap<>((aa, bb)-> aa.exitTime-bb.exitTime    );
                Ri.put(old_new, old_new);
                R.put(high, Ri);

                OV.put(high, new TreeSet<>((aa, bb)-> aa.exitTime-bb.exitTime));
                O.put(high, new TreeSet<>((aa, bb)-> aa.exitTime-bb.exitTime));

                high++;
            }
        }
        else{
            int i = (int)Math.floor(Math.log(r_t/2)/Math.log(1+beta));
            while(i <= Math.ceil(Math.log(2*M_t/delta)/Math.log(1+beta))){
                TreeMap<Point, Point> RVi = new TreeMap<>((aa, bb)-> aa.exitTime-bb.exitTime);
                RVi.put(old_new, old_new);
                RV.put(i, RVi);

                TreeMap<Point, Point> Ri = new TreeMap<>((aa, bb)-> aa.exitTime-bb.exitTime);
                Ri.put(old_new, old_new);
                R.put(i, Ri);

                OV.put(i, new TreeSet<>((aa, bb)-> aa.exitTime-bb.exitTime));
                O.put(i, new TreeSet<>((aa, bb)-> aa.exitTime-bb.exitTime));
                i++;
            }
        }
        // end updating


        for(Integer i : RV.keySet())
        {
            double gamma = Math.pow(1+beta, i);
            // REMOVE OLD POINTS
            ArrayList<Point> ptsToDel = new ArrayList<>();

            if(!RV.get(i).isEmpty() && RV.get(i).firstKey().exitTime <= step){
                OV.get(i).add(RV.get(i).get(RV.get(i).firstKey()));
                RV.get(i).remove(RV.get(i).firstKey());
            }
            if(!R.get(i).isEmpty() && R.get(i).firstKey().exitTime <= step){
                O.get(i).add(R.get(i).get(R.get(i).firstKey()));
                R.get(i).remove(R.get(i).firstKey());
            }
            while(!OV.get(i).isEmpty() && OV.get(i).first().exitTime <= step)
                OV.get(i).remove(OV.get(i).first());
             while(!O.get(i).isEmpty() && O.get(i).first().exitTime <= step)
                O.get(i).remove(O.get(i).first());

            // INSERT NEW POINT p
            ArrayList<Point> D = new ArrayList<>(); // within radius of an attraction pt
            ArrayList<Point> E = new ArrayList<>(); // within radius of a validation pt
            for(Point q : R.get(i).keySet()){
                if( p.distance(q) < delta*gamma/2 ){
                    D.add(q);
                }
            }
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

                    ptsToDel = new ArrayList<>();
                    for(Point q : R.get(i).keySet()){
                        if(q.exitTime <= tOld){
                            O.get(i).add(R.get(i).get(q));
                            ptsToDel.add(q);
                        }
                        else
                            break;
                    }
                    for(Point q : ptsToDel) R.get(i).remove(q);
                    ptsToDel = null;
                    while(!O.get(i).isEmpty() && O.get(i).first().exitTime <= tOld)
                        O.get(i).remove(O.get(i).first());;
                 }
            } else {
                for(Point a : E){
                    //RV.get(i).remove(a);
                    RV.get(i).put(a, p);
                }
            }
            if(D.isEmpty()){
                R.get(i).put(p, p);
            } else {
                for(Point a : D){
                    //R.get(i).remove(a);
                    R.get(i).put(a, p);
                }
            }

            // ENDED INSERTING

            //CHECKS
            if(!(OV.get(i).size() <= k+1))
                throw new RuntimeException("OV size "+i);
            //END OF CHECKS
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


    public TreeMap<Integer, TreeSet<Point>> O, OV;
    public TreeMap<Integer, TreeMap<Point, Point>> R, RV;
    TreeSet<Point> last_points;
    Point first_point;
    public double r_t = 0, M_t = 0;
    int step = 0, wSize, k;
    public double beta, delta;
    Diameter diameter;
}
