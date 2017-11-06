package ch.ethz.inf.vs.a3.message;

import java.util.Comparator;

import ch.ethz.inf.vs.a3.clock.VectorClock;
import ch.ethz.inf.vs.a3.clock.VectorClockComparator;
import ch.ethz.inf.vs.a3.message.Message;

/**
 * Message comparator class. Use with PriorityQueue.
 */

public class MessageComparator implements Comparator<Message> {

    @Override
    public int compare(Message lhs, Message rhs) {
        // Write your code here
        VectorClockComparator myVCC = new VectorClockComparator();
        VectorClock vectorClock1 = lhs.getTimestamp();
        VectorClock vectorClock2 = rhs.getTimestamp();

        return myVCC.compare(vectorClock1, vectorClock2);
    }


}
