package ch.ethz.inf.vs.a3.clock;

import ch.ethz.inf.vs.a3.clock.Clock;

/**
 * Created by Josua on 10/30/17.
 */

public class LamportClock implements Clock {

    private int time;

    @Override
    public void update(Clock other) {
        if(other instanceof LamportClock) {
            LamportClock otherClock = (LamportClock) other;
            if(happenedBefore(otherClock)) {
                this.time = otherClock.time;
            }
        } else {
            throw new IllegalArgumentException("Clock object has to be of type LamportClock (Was " + other.getClass().toString() + ")");
        }
    }

    @Override
    public void setClock(Clock other) {
        if(other instanceof LamportClock) {
            LamportClock otherClock = (LamportClock) other;
            this.time = otherClock.time;
        } else {
            throw new IllegalArgumentException("Clock object has to be of type LamportClock (Was " + other.getClass().toString() + ")");
        }
    }

    @Override
    public void tick(Integer pid) {
        time++;
    }

    @Override
    public boolean happenedBefore(Clock other) {
        if(other instanceof LamportClock) {
            LamportClock otherClock = (LamportClock) other;
            return this.time < otherClock.time;
        }
        return false;
    }

    @Override
    public String toString() {
        return Integer.toString(time);
    }

    @Override
    public void setClockFromString(String clock) {
        try{
            this.time = Integer.parseInt(clock);
        } catch (Exception e){
            System.err.println("String has to be numeric. Was: " + clock);
        }
    }

    public void setTime(int time){
        this.time = time;
    }

    public int getTime(){
        return time;
    }
}
