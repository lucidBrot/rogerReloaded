package ch.ethz.inf.vs.a3.vsminkerchat;

import java.net.DatagramPacket;
import java.util.ArrayList;

/**
 * Created by christianknieling on 30.10.17.
 */

public interface AsyncResponse {
    void processFinish(ArrayList<DatagramPacket> myPacket);
}
