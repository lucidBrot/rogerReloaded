package ch.ethz.inf.vs.a3.clock;

import android.util.JsonReader;
import android.util.SparseArray;
import android.util.SparseIntArray;

import java.util.HashMap;
import java.util.Map;

import ch.ethz.inf.vs.a3.clock.Clock;

/**
 * Created by Josua on 10/30/17.
 */

public class VectorClock implements Clock {

    private Map<Integer,Integer> vector = new HashMap<>();

    @Override
    public void update(Clock other) {
        if (other instanceof VectorClock) {
            VectorClock otherVector = (VectorClock) other;
            for(Integer key : otherVector.vector.keySet()){
                Integer otherVal = otherVector.vector.get(key);

                if(vector.containsKey(key)){
                    if(vector.get(key) < otherVal){
                        vector.put(key, otherVal);
                    }
                } else {
                    vector.put(key, otherVal);
                }
            }
        } else {
            throw new IllegalArgumentException("Clock object has to be of type VectorClock (Was " + other.getClass().toString() + ")");
        }
    }

    @Override
    public void setClock(Clock other) {
        if (other instanceof VectorClock) {
            this.vector = ((VectorClock) other).vector;
        } else {
            throw new IllegalArgumentException("Clock object has to be of type VectorClock (Was " + other.getClass().toString() + ")");
        }
    }

    @Override
    public void tick(Integer pid) {
        int newValue = vector.get(pid) + 1;
        vector.put(pid, newValue);
    }

    @Override
    public boolean happenedBefore(Clock other) {
        if (other instanceof VectorClock) {
            VectorClock otherClock = (VectorClock) other;
            for (Integer key : otherClock.vector.keySet()) {
                if(vector.containsKey(key)){
                    if (vector.get(key) > otherClock.vector.get(key)){
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Integer key : vector.keySet()) {
            if (first) {
                first = false;
            } else {
                sb.append(",");
            }
            sb.append("\"");
            sb.append(key);
            sb.append("\":");
            sb.append(vector.get(key));
        }
        return "{" + sb.toString() + "}";
    }

    @Override
    public void setClockFromString(String clock) {
        String[] items = clock.split(",");
        Map<Integer, Integer> newMap = new HashMap<>();
        for(String item : items){
            String[] keyValue = item.split("\"");
            if(keyValue.length == 3) {
                String key = keyValue[1];
                String value = keyValue[2].split(":")[1].split("\\}")[0];
                try {

                    newMap.put(Integer.parseInt(key), Integer.parseInt(value));
                } catch (Exception e) {
                    System.err.println("Could not parse String: " + clock);
                    return;
                }
            }
        }
        vector = newMap;
    }

    public int getTime(Integer pid){
        if(vector.containsKey(pid)) {
            return vector.get(pid);
        }
        return -1;

    }

    public void addProcess(Integer pid, int time){
        if(!vector.containsKey(pid))
            vector.put(pid, time);
    }
}
