package src.core.css;

import src.core.utils.*;
import java.util.*;

public class Diameter
{
    public Diameter(double _eps, int _wSize)
    {
        eps = _eps;
        wSize = _wSize;
        cold = new TreeMap<>();
        cnew = new TreeMap<>();
        q = new TreeMap<>();
        r = new TreeMap<>();
    }

    public double query()
    {
        if(cnew.isEmpty()) return 0;
        for(int i=cnew.firstKey(); i<=cnew.lastKey(); i++){
            if(cnew.get(i) == null)
                return Math.pow(1+eps, i-1); // r < OPT < 3*r*(1+eps)
        }
        return -1;
    }

    public ArrayList<Point> queryPoints()
    {
        if(cnew.isEmpty()) return null;
        for(int i=cnew.firstKey(); i<=cnew.lastKey(); i++){
            if(cnew.get(i) == null){
                ArrayList<Point> ans = new ArrayList<>(2);
                ans.add(cnew.get(i-1));
                ans.add(cold.get(i-1));
                return ans;
            }
        }
        return null;
    }

    public void update(Point p)
    {
        if(pt2 == null){
            pt2 = p;
            return;
        }
		if(pt1 == null){
			pt1 = p;
			return;
		}
        double r_t = pt1.distance(p)+1e-9;
        // update lower bound
        ArrayList<Integer> gams = new ArrayList<>();
        for(Integer gam : q.keySet())
            gams.add(gam);
        for(Integer gam : gams){
            if(gam < Math.floor(Math.log(r_t)/Math.log(1+eps)) ){
                cold.remove(gam);
                cnew.remove(gam);
                q.remove(gam);
                r.remove(gam);
            }
        }
        if(!q.isEmpty()){
            int low = q.firstKey() -1;
            while(low >= (int)Math.floor(Math.log(r_t)/Math.log(1+eps)) ){
                cold.put(low, pt2);
                r.put(low, pt2);
                q.put(low, pt2);
                cnew.put(low, pt1);
                low--;
            }
        }
        // ended updating lower bound

        // add p for each gamma
        int i = (int)Math.floor(Math.log(r_t)/Math.log(1+eps));
        double M_t = 3*(1+eps)*r_t;
        while(true){
            double gamma = Math.pow(1+eps, i);
            // reached upper bound, cleanup and exit
            if(gamma > M_t){
                for(int j = i; j <= q.lastKey(); j++){
                    cold.remove(j);
                    cnew.remove(j);
                    r.remove(j);
                    q.remove(j);
                }
                break;
            }

            // need to rebuild if implicitly stored
            if(q.isEmpty() || i > q.lastKey()){
                cold.put(i, pt2);
                r.put(i, pt2);
                q.put(i, pt2);
                cnew.put(i, null);
            }

            // delete old points
            if(cold.get(i).exitTime <= step){
                if(cnew.get(i) != null){
                    if(cold.get(i) == q.get(i)){
                        cold.put(i, r.get(i));
                        cnew.put(i, null);
                    }
                    else{
                        cold.put(i, q.get(i));
                        cnew.put(i, null);
                    }
                }
                else{
                    cold.put(i, r.get(i));
                }
            }
            // insert p
            if(cnew.get(i) == null){
                if(p.distance(r.get(i)) > gamma){
                    cold.put(i, r.get(i));
                    q.put(i, r.get(i));
                    cnew.put(i, p);
                }
                else if(p.distance(cold.get(i)) > gamma){
                    q.put(i, r.get(i));
                    cnew.put(i, p);
                }
            }
            else{
                if(p.distance(r.get(i)) > gamma){
                    cold.put(i, r.get(i));
                    q.put(i, r.get(i));
                    cnew.put(i, p);
                }
                else if(p.distance(cnew.get(i)) > gamma){
                    cold.put(i, cnew.get(i));
                    q.put(i, r.get(i));
                    cnew.put(i, p);
                }
                else if(p.distance(q.get(i)) > gamma){
                    if(cold.get(i) == q.get(i)){
                        cold.put(i, q.get(i));
                        q.put(i, r.get(i));
                        cnew.put(i, p);
                    }
                }
            }

            // update r
            r.put(i, p);

            if(cnew.get(i) != null){ // diameter <= 3*gamma
                M_t = 3*gamma*(1+eps);
            }

            i++;
        }

        pt2 = pt1;
		pt1 = p;
        step++;
    }


    public TreeMap<Integer, Point> cold, cnew, q, r;
    Point pt2, pt1;
    int step = 0, wSize;
    double eps;
    int iters;
}
