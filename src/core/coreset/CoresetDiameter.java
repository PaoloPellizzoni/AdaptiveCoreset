package src.core.coreset;

import src.core.utils.*;
import java.util.*;

public class CoresetDiameter
{
    public CoresetDiameter(double _beta, double _eps, int _wSize){
        wSize = _wSize;
        cor = new CoresetOblivious(_beta, _eps/2, _wSize, 1);
    }

    public void update(Point p){
        cor.update(p);
    }

    public ArrayList<Point> query(){
        return completeSearchDiameter(cor.query());
    }

    public ArrayList<Point> approxQuery(){
        return approxDiameter(cor.query());
    }

    public static ArrayList<Point> completeSearchDiameter(List<Point> l){
        double best = 0;
        ArrayList<Point> ans = new ArrayList<>(2);
        ans.add(null); ans.add(null);
        for(Point p1 : l){
            for(Point p2 : l){
                double d = p1.distance(p2);
                if(d > best){
                    best = d;
                    ans.set(0, p1);
                    ans.set(1, p2);
                }
            }
        }
        return ans;
    }

    public static ArrayList<Point> approxDiameter(List<Point> l){
        double best = 0;
        ArrayList<Point> ans = new ArrayList<>(2);
        ans.add(null); ans.add(null);
        Point p1 = l.get(0);
        ans.set(0, p1);
        for(Point p2 : l){
            double d = p1.distance(p2);
            if(d > best){
                best = d;
                ans.set(1, p2);
            }
        }

        p1 = ans.get(1);
        for(Point p2 : l){
            double d = p1.distance(p2);
            if(d > best){
                best = d;
                ans.set(0, p2);
            }
        }
        return ans;
    }

    int wSize;
    public CoresetOblivious cor;
}
