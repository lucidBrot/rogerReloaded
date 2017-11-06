package ch.ethz.inf.vs.a3.clock;

import java.util.Comparator;


public class VectorClockComparator implements Comparator<VectorClock> {

    @Override
    public int compare(VectorClock lhs, VectorClock rhs) {
        if(lhs.toString().equals(rhs.toString())){
            return 0;
        }
        if(lhs.happenedBefore(rhs)){
            return -1;
        } else {
            return 1;
        }
    }
}
