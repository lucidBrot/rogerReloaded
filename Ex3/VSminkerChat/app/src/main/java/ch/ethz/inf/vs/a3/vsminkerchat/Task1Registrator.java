package ch.ethz.inf.vs.a3.vsminkerchat;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.UUID;

/**
 * AsyncTask Task1Registrator. The background task returns true if registration was successful after this.tries retries
 */
public class Task1Registrator extends AsyncTask<Void, Void, Boolean> {
    private int tries;
    private String uuid; // random identifier
    private int serverPort;
    private InetAddress serverIP;
    private DatagramSocket socket;
    private int timeout = 2000; // miliseconds
    private int RESPONSEPACKETSIZE = 1000; // TODO: handle buffer overflow / what when not received all data?
    private String username;

    /**
     * Generate a Task1Registrator AsyncTask
     * @param serverIP
     * @param serverPort
     */
    Task1Registrator(String serverIP, int serverPort, int tries, int timeout, String username){
        super();
        this.tries = tries;
        this.uuid = UUID.randomUUID().toString();
        this.serverPort = serverPort;
        try {
            this.serverIP = InetAddress.getByName(serverIP); // validates IP format
        } catch (UnknownHostException e) { // If this happens, the app is wrong. DO NOT PASS BAD ADDRESSES
            Log.e("Task1/Registrator", "invalid IP Address");
            e.printStackTrace();
        }
        this.timeout = timeout;
        this.username = username;
    }

    /**
     * Generate a Task1Registrator with default values 10.0.2.2:4446, 5 tries to connect, timeout of 2000 ms, username Roger
     */
    Task1Registrator(){
        this("10.0.2.2",4446,5, 2000, "Roger");
    }


    @Override
    protected Boolean doInBackground(Void... voids) {
        Log.d("Task1/Registrator", "was called asynchronously");

        // try to connect until it works or we tried <i>tries</i> times
        ResponseObject responseObject = new ResponseObject(false, null);
        for(int i=0; i<tries; i++){
            Log.d("Task1/Registrator", "Establishing connection. Try "+(i+1)+" / "+tries);
            responseObject = register();
            if(responseObject.isSuccess()) break;
        }
        // packet is now in responseObject
        return responseObject.isSuccess();
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        Log.d("Task1/Registrator", "finished registration successfully? : "+aBoolean.toString());
    }

    private ResponseObject register(){
        try {
            // Socket on a random port
            socket = new DatagramSocket();


            byte [] data = generateRegisterRequestString().getBytes(); // prepare request to send
            DatagramPacket packet = new DatagramPacket(data, data.length, this.serverIP, this.serverPort);

            socket.send(packet);
            Log.d("Task1/Registrator", "sent packet");
            socket.setSoTimeout(this.timeout);

            // prepare packet to recieve answer
            packet.setData(new byte[RESPONSEPACKETSIZE]);

            socket.receive(packet); // blocking call

            Log.d("Task1/Registrator", "Received answer: "+ new String(packet.getData()).trim()); // TODO: parse answer and react to it
            /*
            // Some logs. first try was success, second was with same username, last was with new uuid and same name

            10-25 19:09:23.286 26563-26837/ch.ethz.inf.vs.a3.vsminkerchat D/Task1/Registrator: Received answer: {"header":{"type":"ack","uuid":"6ec1010d-4155-4525-8bc2-30a7f828382d","username":"server","timestamp
            10-25 19:09:23.291 26563-26563/ch.ethz.inf.vs.a3.vsminkerchat D/Task1/Registrator: finished registration successfully? : true

            10-25 19:09:40.590 26563-27104/ch.ethz.inf.vs.a3.vsminkerchat D/Task1/Registrator: Received answer: {"header":{"type":"error","uuid":"6ec1010d-4155-4525-8bc2-30a7f828382d","username":"server","timesta
            10-25 19:09:40.591 26563-26563/ch.ethz.inf.vs.a3.vsminkerchat D/Task1/Registrator: finished registration successfully? : true

            19:11:26.191 28339-28742/ch.ethz.inf.vs.a3.vsminkerchat D/Task1/Registrator: Received answer: {"header":{"type":"error","uuid":"6ec1010d-4155-4525-8bc2-30a7f828382d","username":"server","timesta
            10-25 19:11:26.191 28339-28339/ch.ethz.inf.vs.a3.vsminkerchat D/Task1/Registrator: finished registration successfully? : true

             */
            return new ResponseObject(true, packet);

        } catch (SocketTimeoutException e){
            Log.d("Task1/Registrator","Socket Timeout while waiting to receive ACK");
            return new ResponseObject(false,null);
        }
        catch (SocketException e) {
            Log.e("Task1/Registrator","Socket Exception: "+e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Task1/Registrator","I/O Exception: "+e.getMessage());
            e.printStackTrace();
        }
        return new ResponseObject(false,null);
    }

    private String generateRegisterRequestString(){
        return  "{\"header\":{" +
                "\"username\": \""+this.username+"\"," +
                "\"uuid\": \""+this.uuid +"\"," +
                "\"timestamp\": \"{}\"," +
                "\"type\": \"register\"" +
                "}," +
                "\"body\": {}" +
                "}";
    }

    private class ResponseObject {
        private boolean success;
        private DatagramPacket packet;

        ResponseObject(boolean s, DatagramPacket p){
            this.success=s;
            this.packet = p;
        }

        public DatagramPacket getPacket() {
            return packet;
        }

        public void setPacket(DatagramPacket packet) {
            this.packet = packet;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }
    }

}
