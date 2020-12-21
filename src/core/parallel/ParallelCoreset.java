package src.core.parallel;

import src.core.utils.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class ParallelCoreset
{
    public ParallelCoreset(double _beta, double _eps, int _wSize, int _k, double _mD, double _MD, int _nWorkers)
    {
        beta = _beta;
        delta = _eps/(1+beta);
        wSize = _wSize;
        k = _k;
        minDist = _mD;
        maxDist = _MD;
        iters = (int)(Math.log(maxDist/minDist)/Math.log(1+beta) + 1);
        nWorkers = _nWorkers;
        workers = new ThreadWorker[nWorkers];
        int id = 0;
        for(double gamma = minDist; gamma < maxDist;){
            ArrayList<TreeSet<Point>> Ob = new ArrayList<>(), OVb = new ArrayList<>();
            ArrayList<TreeMap<Point, Point>> Rb = new ArrayList<>(), RVb = new ArrayList<>();
            double gamma0 = gamma;
            for(int j = 0; j <= Math.ceil(iters/nWorkers) && gamma < maxDist; j++){
                Ob.add(new TreeSet<>((aa, bb)-> aa.exitTime-bb.exitTime));
                OVb.add(new TreeSet<>((aa, bb)-> aa.exitTime-bb.exitTime));
                Rb.add(new TreeMap<>((aa, bb)-> aa.exitTime-bb.exitTime));
                RVb.add(new TreeMap<>((aa, bb)-> aa.exitTime-bb.exitTime));
                gamma*=(1+beta);
            }
            workers[id] = new ThreadWorker(Ob, OVb, Rb, RVb, wSize, k, gamma0, delta, beta);
            id++;
        }
    }

    public void shutdownWorkers(){
        for(ThreadWorker utw : workers){
            utw.shutdown();
        }
    }

    public ArrayList<Point> query()
    {
        ArrayList<Point> ans = null;
        double r = Double.POSITIVE_INFINITY;
        AtomicInteger counter = new AtomicInteger(0);
        for(ThreadWorker worker : workers){
            worker.computeQuery(counter);
        }
        // waits for all threads to finish
        synchronized(counter){
            while(counter.get() < nWorkers){
                try{
                    wait();
                } catch(Exception e){}
            }
        }
        for(ThreadWorker worker : workers){
            double tmp = worker.queryRadius();
            if(tmp < r){
                r = tmp;
                ans = worker.queryAns();
            }
        }
        return ans;
    }


    public void update(Point p)
    {
        p.exitTime = step + wSize;
        int id = 0;
        AtomicInteger counter = new AtomicInteger(0);
        for(ThreadWorker worker : workers){
            worker.update(p, step, counter);
        }
        // waits for all threads to finish
        synchronized(counter){
            while(counter.get() < nWorkers){
                try{
                    wait();
                } catch(Exception e){}
            }
        }

        step++; //next step
    }


    ThreadWorker[] workers;
    int step = 0, wSize, k;
    double beta, delta;
    double minDist, maxDist;
    int iters;
    int nWorkers;
}



class ThreadWorker extends Thread
{
    public ThreadWorker(ArrayList<TreeSet<Point>> _O, ArrayList<TreeSet<Point>> _OV, ArrayList<TreeMap<Point, Point>> _R, ArrayList<TreeMap<Point, Point>> _RV,
                                int _wSize, int _k, double _gamma0, double _delta, double _beta){
        O = _O; OV = _OV;
        R = _R; RV = _RV;
        wSize = _wSize;
        k = _k;
        gamma0 = _gamma0;
        delta = _delta;
        beta = _beta;
        start();
    }

    public synchronized void shutdown(){
        run = false;
        this.notify();
    }

    public void run(){
        while(run){
            synchronized(this){
                while(task==0 && run){
                    try{
                        wait();
                    } catch(Exception e){}
                }
                if(run){
                    if(task == 1)
                        update();
                    else
                        privQuery();
                }
            }
        }
    }

    public synchronized void computeQuery(AtomicInteger _count){
        count = _count;
        task = 2;
        notify();
    }

    public synchronized ArrayList<Point> queryAns(){
        return queryAns;
    }
    public synchronized double queryRadius(){
        return queryRadius;
    }

    private void privQuery(){
        int i = 0;
        double gamma;
        for(gamma = gamma0; i< RV.size(); gamma*=(1+beta))
        {
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
            for(Point p : RV.get(i).values())
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
                break;
            i++;
        }
        if(i == RV.size()){
            queryRadius = Double.POSITIVE_INFINITY;
            queryAns = null;
        }
        else{
            ArrayList<Point> core = new ArrayList<>();
            for(Point p : R.get(i).values())
                core.add(p);
            for(Point p : O.get(i))
                core.add(p);
            queryRadius = gamma;
            queryAns = core;
        }
        task = 0; //completed task
        //notify the main thread
        count.incrementAndGet();
        synchronized(count){
            count.notify();
        }

    }

    public synchronized void update(Point _p,  int _step, AtomicInteger _count){
        p = _p;
        step = _step;
        count = _count;
        task = 1;
        notify();
    }

    private void update(){
        double gamma = gamma0;
        for(int i=0; i<RV.size(); i++)
        {
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
            gamma *= (1+beta);
        }
        queryRadius = 0;
        queryAns = null;
        task = 0; //completed task
        //notify the main thread
        count.incrementAndGet();
        synchronized(count){
            count.notify();
        }
    }

    ArrayList<TreeSet<Point>> O, OV;
    ArrayList<TreeMap<Point, Point>> R, RV;
    Point p;
    int step, wSize, k;
    double gamma0, delta, beta;
    int task = 0;
    boolean run = true;
    double queryRadius;
    ArrayList<Point> queryAns;
    AtomicInteger count;
}
