package src.core.utils;

import java.util.*;
import java.io.*;

public class DataGenerator
{
    public static void main(String[] args) throws Exception{
        int wSize = 10000;
        /* Produce obl */
        PrintWriter io = new PrintWriter("data/alpha.dat");

        for(int i=0; i<3*wSize; i++){
            if(i%10 == 0){
                for(int j=0; j<5; j++)
                    io.print(Math.random()*100+" ");
                for(int j=0; j<5; j++)
                    io.print("0.0 ");
            }
            else{
                for(int j=0; j<5; j++)
                    io.print(Math.random()*10+" ");
                for(int j=0; j<5; j++)
                    io.print("0.0 ");
            }
            io.println();
        }
        for(int i=0; i<3*wSize; i++){
            if(i%10 == 0){
                for(int j=0; j<5; j++)
                    io.print(Math.random()*10+" ");
                for(int j=0; j<5; j++)
                    io.print("0.0 ");
            }
            else{
                for(int j=0; j<5; j++)
                    io.print(Math.random()*0.01+" ");
                for(int j=0; j<5; j++)
                    io.print("0.0 ");
            }
            io.println();
        }
        for(int i=0; i<3*wSize; i++){
            if(i%10 == 0){
                for(int j=0; j<5; j++)
                    io.print(Math.random()*100+" ");
                for(int j=0; j<5; j++)
                    io.print("0.0 ");
            }
            else{
                for(int j=0; j<5; j++)
                    io.print(Math.random()*10+" ");
                for(int j=0; j<5; j++)
                    io.print("0.0 ");
            }
            io.println();
        }
        io.close();
        //*/

        /* produce dd seq
        PrintWriter io = new PrintWriter("data/dd_seq.dat");
        for(int d=1; d<10; d+=1){
            for(int i=0; i<2.5*wSize; i++){
                if(i%10 == 0 || i%10 == 1){
                    io.print(Math.random()*0.1+" ");
                    for(int j=1; j<100; j++)
                        io.print("0.0 ");
                }
                else{
                    for(int j=0; j<d; j++)
                        io.print(Math.random()+" ");
                    for(int j=0; j<100-d; j++)
                        io.print("0.0 ");
                }
                io.println();
            }

        }
        int d = 10;
        for(int i=0; i<2.5*wSize; i++){
            if(i%10 == 0 || i%10 == 1){
                io.print(Math.random()*0.1+" ");
                for(int j=1; j<100; j++)
                    io.print("0.0 ");
            }
            else{
                for(int j=0; j<d; j++)
                    io.print(Math.random()+" ");
                for(int j=0; j<100-d; j++)
                    io.print("0.0 ");
            }
            io.println();
        }
        d = 15;
        for(int i=0; i<2.5*wSize; i++){
            if(i%10 == 0 || i%10 == 1){
                io.print(Math.random()*0.1+" ");
                for(int j=1; j<100; j++)
                    io.print("0.0 ");
            }
            else{
                for(int j=0; j<d; j++)
                    io.print(Math.random()+" ");
                for(int j=0; j<100-d; j++)
                    io.print("0.0 ");
            }
            io.println();
        }
        d = 20;
        for(int i=0; i<2.5*wSize; i++){
            if(i%10 == 0 || i%10 == 1){
                io.print(Math.random()*0.1+" ");
                for(int j=1; j<100; j++)
                    io.print("0.0 ");
            }
            else{
                for(int j=0; j<d; j++)
                    io.print(Math.random()+" ");
                for(int j=0; j<100-d; j++)
                    io.print("0.0 ");
            }
            io.println();
        }
        d = 25;
        for(int i=0; i<2.5*wSize; i++){
            if(i%10 == 0 || i%10 == 1){
                io.print(Math.random()*0.1+" ");
                for(int j=1; j<100; j++)
                    io.print("0.0 ");
            }
            else{
                for(int j=0; j<d; j++)
                    io.print(Math.random()+" ");
                for(int j=0; j<100-d; j++)
                    io.print("0.0 ");
            }
            io.println();
        }



        io.close();
        //*/

        /* produce dd
        PrintWriter io = new PrintWriter("data/dd.dat");
        int d = 1;
        for(int i=0; i<3*wSize; i++){
            if(i%10 == 0 || i%10 == 1){
                io.print(Math.random()*0.1+" ");
                for(int j=1; j<100; j++)
                    io.print("0.0 ");
            }
            else{
                for(int j=0; j<d; j++)
                    io.print(Math.random()+" ");
                for(int j=0; j<100-d; j++)
                    io.print("0.0 ");
            }
            io.println();
        }
        d = 10;
        for(int i=0; i<3*wSize; i++){
            if(i%10 == 0 || i%10 == 1){
                io.print(Math.random()*0.1+" ");
                for(int j=1; j<100; j++)
                    io.print("0.0 ");
            }
            else{
                for(int j=0; j<d; j++)
                    io.print(Math.random()+" ");
                for(int j=0; j<100-d; j++)
                    io.print("0.0 ");
            }
            io.println();
        }
        d = 1;
        for(int i=0; i<3*wSize; i++){
            if(i%10 == 0 || i%10 == 1){
                io.print(Math.random()*0.1+" ");
                for(int j=1; j<100; j++)
                    io.print("0.0 ");
            }
            else{
                for(int j=0; j<d; j++)
                    io.print(Math.random()+" ");
                for(int j=0; j<100-d; j++)
                    io.print("0.0 ");
            }
            io.println();
        }




        io.close();
        //*/
    }
}
