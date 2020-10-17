package com.example.clientframework.Tasks;

import java.util.Vector;

public class SortTask {

    public static Vector<Integer> performTask(Vector<Integer> vector, int size){
        for(int i=0;i<size-1;i++)
            for(int j=0;j<size-i-1;j++){
                if(vector.get(j) > vector.get(j+1)){
                    int temp = vector.get(j);
                    vector.set(j,vector.get(j+1));
                    vector.set(j+1,temp);
                }
            }
        return vector;
    }
}
