package src.core.css;

import src.core.utils.*;
import java.util.*;

public class CSS
{
    public CSS(double _eps, int _wSize, int _k, double _mD, double _MD)
    {
        eps = _eps;
        wSize = _wSize;
        k = _k;
        minDist = _mD;
        maxDist = _MD;
        iters = (int)(Math.log(maxDist/minDist)/Math.log(1+eps) + 1);
        RV = new HashMap[iters];
        OV = new TreeSet[iters];
        for(int i = 0; i < iters; i++){
            RV[i] = new HashMap<>();
            OV[i] = new TreeSet<>((aa, bb)-> aa.exitTime-bb.exitTime);
        }
    }

    public ArrayList<Point> query()
    {
        int i = 0;
        for(double gamma = minDist; gamma < maxDist; gamma*=(1+eps))
        {
            ArrayList<Point> C = new ArrayList<>();
            for(Point p : RV[i].keySet())
            {
                C.add(p);
            }
            for(Point p : OV[i])
            {
                if(C.size() > k)
                    break;
                double mind = maxDist+1;
                for(Point q : C)
                    mind = Math.min(mind, p.distance(q));
                if(mind > 2*gamma)
                    C.add(p);
            }
            for(Point p : RV[i].values())
            {
                if(C.size() > k)
                    break;
                double mind = maxDist+1;
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
        int i = 0;

        for(double gamma = minDist; gamma < maxDist; gamma*=(1+eps))
        {
            // REMOVE OLD POINTS
            ArrayList<Point> ptsToDel = new ArrayList<>();
            for(Point q : RV[i].keySet()){
                if(q.exitTime <= step){
                    OV[i].add(RV[i].get(q));
                    ptsToDel.add(q);
                    //break; //only one exits
                }
            }
            for(Point q : ptsToDel) RV[i].remove(q);

            while(!OV[i].isEmpty() && OV[i].first().exitTime <= step)
                OV[i].remove(OV[i].first());

            ptsToDel = null;
            // ENDED REMOVING OLD POINTS

            // INSERT NEW POINT p
            ArrayList<Point> E = new ArrayList<>(); // within radius of an attr pt
            for(Point q : RV[i].keySet()){
                if( p.distance(q) < gamma*2 ){
                    E.add(q);
                }
            }

            if(E.isEmpty()){
                RV[i].put(p, p);
                if(RV[i].size() > k+1){ // keep size <= k+1
                    Point vOld = null;
                    for(Point q : RV[i].keySet()){
                        if(vOld==null || q.exitTime < vOld.exitTime)
                            vOld = q;
                    }
                    OV[i].add(RV[i].get(vOld));
                    RV[i].remove(vOld);
                }
                if(RV[i].size() > k){ // surely can't find a k cluster, so delete
                    int tOld = Integer.MAX_VALUE;   // all the points older than oldest attr pt
                    for(Point q : RV[i].keySet()){
                        tOld = Math.min(tOld, q.exitTime);
                    }

                    while(!OV[i].isEmpty() && OV[i].first().exitTime <= tOld)
                        OV[i].remove(OV[i].first());

                    ptsToDel = null;
                }
            } else {
                for(Point a : E){
                    RV[i].put(a, p);
                }
            }

            i++; // next gamma
        }
        step++; //next step
    }


    public TreeSet<Point>[] OV;
    public HashMap<Point, Point>[] RV;
    int step = 0, wSize, k;
    double eps, alpha;
    public double minDist, maxDist;
    int iters;
}
