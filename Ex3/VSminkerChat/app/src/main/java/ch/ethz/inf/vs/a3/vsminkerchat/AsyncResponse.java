package ch.ethz.inf.vs.a3.vsminkerchat;

import java.net.DatagramPacket;

/**
 * Created by christianknieling on 30.10.17.
 */

public interface AsyncResponse {
    void processFinish(DatagramPacket myPacket);
}
