package ch.ethz.inf.vs.a3.message;

import ch.ethz.inf.vs.a3.clock.VectorClock;

/**
 * Created by christianknieling on 30.10.17.
 */

public class Message {
    public VectorClock timestamp;
    public String label;
    public String type;

    public VectorClock getTimestamp() {
        return timestamp;
    }

    public String getLabel() {
        return label;
    }

    public String getType() {
        return type;
    }

    public Message(String valuesIn, String labelIn){
        timestamp  = new VectorClock();
        timestamp.setClockFromString(valuesIn);
        label = labelIn;
    }

}
