/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coreset;

/**
 *
 * @author ppell
 */
import java.util.*;

public class Coreset
{
	public Coreset(double _eps, double _alpha, int _wSize, int _k)
	{
		eps = _eps;
		alpha = _alpha;
		wSize = _wSize;
		k = _k;
		iters = (int)(Math.log(maxDist/minDist)/Math.log(1+eps) + 1);
		RV = new TreeMap[iters];
		OV = new TreeSet[iters];
		R = new TreeMap[iters];
		O = new TreeSet[iters];
		for(int i = 0; i < iters; i++){
			RV[i] = new TreeMap<>((aa, bb)-> aa.exitTime-bb.exitTime);
			OV[i] = new TreeSet<>((aa, bb)-> aa.exitTime-bb.exitTime);
			R[i] = new TreeMap<>((aa, bb)-> aa.exitTime-bb.exitTime);
			O[i] = new TreeSet<>((aa, bb)-> aa.exitTime-bb.exitTime);
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
				break;
			i++;
		}
		ArrayList<Point> core = new ArrayList<>();
		for(Point p : R[i].values())
			core.add(p);
		for(Point p : O[i])
			core.add(p);
		return core;
	}	
	
	public void update(Point p)
	{
		p.exitTime = step + wSize;
		int i = 0;
		
		for(double gamma = minDist; gamma < maxDist; gamma*=(1+eps))
		{
			// REMOVE OLD POINTS
			ArrayList<Point> ptsToDel = new ArrayList<>();
            
			if(!RV[i].isEmpty() && RV[i].firstKey().exitTime <= step){
                OV[i].add(RV[i].get(RV[i].firstKey()));
                RV[i].remove(RV[i].firstKey());
            }
            if(!R[i].isEmpty() && R[i].firstKey().exitTime <= step){
                O[i].add(R[i].get(R[i].firstKey()));
                R[i].remove(R[i].firstKey());
            }
            while(!OV[i].isEmpty() && OV[i].first().exitTime <= step)
                OV[i].remove(OV[i].first());
             while(!O[i].isEmpty() && O[i].first().exitTime <= step)
                O[i].remove(O[i].first());
			
			// INSERT NEW POINT p
			ArrayList<Point> D = new ArrayList<>(); // within radius of an attraction pt
			ArrayList<Point> E = new ArrayList<>(); // within radius of a validation pt
			for(Point q : R[i].keySet()){
				if( p.distance(q) < alpha*gamma/2 ){
					D.add(q);
				}
			}
			for(Point q : RV[i].keySet()){
				if( p.distance(q) < gamma*2 ){
					E.add(q);
				}
			}
			if(E.isEmpty()){
				RV[i].put(p, p);
				if(RV[i].size() > k+1){ // keep size <= k+1
					Point vOld = RV[i].firstKey();
					OV[i].add(RV[i].get(vOld));
					RV[i].remove(vOld);
					
				}
				if(RV[i].size() > k){ // surely can't find a k cluster, so delete
					int tOld = RV[i].firstKey().exitTime;   
					
                    while(!OV[i].isEmpty() && OV[i].first().exitTime <= tOld)
                        OV[i].remove(OV[i].first());
					
					ptsToDel = new ArrayList<>();
					for(Point q : R[i].keySet()){
						if(q.exitTime <= tOld){
							O[i].add(R[i].get(q));
							ptsToDel.add(q);
						}
                        else
                            break;
					}
					for(Point q : ptsToDel) R[i].remove(q);
					ptsToDel = null;
                    while(!O[i].isEmpty() && O[i].first().exitTime <= tOld)
                        O[i].remove(O[i].first());;
				 }
			} else {
				for(Point a : E){
					//RV[i].remove(a);
					RV[i].put(a, p);
				}
			}
			if(D.isEmpty()){
				R[i].put(p, p);
			} else {
				for(Point a : D){
					//R[i].remove(a);
					R[i].put(a, p);
				}
			}
			
			// ENDED INSERTING
			
			//CHECKS
			if(!(OV[i].size() <= k+1))
				throw new RuntimeException("OV size "+i);		
			//END OF CHECKS
			
			
			i++; // next gamma
		}
		step++; //next step
	}
	

    TreeSet<Point>[] O, OV;
	TreeMap<Point, Point>[] R, RV;
	int step = 0, wSize, k;
	double eps, alpha;
	double minDist = 0.001, maxDist = 1000;
	int iters;
}