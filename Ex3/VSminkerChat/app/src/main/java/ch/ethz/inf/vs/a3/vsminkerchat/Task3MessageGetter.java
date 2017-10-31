package ch.ethz.inf.vs.a3.vsminkerchat;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;
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
public class Task3MessageGetter extends AsyncTask<Void, Void, DatagramPacket> {
    private int tries;
    private String uuid; // random identifier
    private int serverPort;
    private InetAddress serverIP;
    private DatagramSocket socket;
    private int timeout = 2000; // miliseconds
    private int RESPONSEPACKETSIZE = 1000; // TODO: handle buffer overflow / what when not received all data?
    private String username;
    private Context context;

    public AsyncResponse delegate = null;

    /**
     * Generate a Task1Registrator AsyncTask
     * @param serverIP
     * @param serverPort
     */
    Task3MessageGetter(String serverIP, int serverPort, int tries, int timeout, String username, Context context){
        super();
        this.tries = tries;
        this.uuid = MainActivity.uuid; // TODO: should UUID be regenerated on each registration?
        this.serverPort = serverPort;
        try {
            this.serverIP = InetAddress.getByName(serverIP); // validates IP format
        } catch (UnknownHostException e) { // If this happens, the app is wrong. DO NOT PASS BAD ADDRESSES
            Log.e("Task3/MessageGetter", "invalid IP Address");
            e.printStackTrace();
        }
        this.timeout = timeout;
        this.username = username;
        this.context = context;
    }

    /**
     * Generate a Task3/MessageGetter with default values 10.0.2.2:4446, 5 tries to connect, timeout of 2000 ms, username Roger
     */
    Task3MessageGetter(Context context){
        this("10.0.2.2",4446,5, 2000, "roger", context);
    }


    @Override
    protected DatagramPacket doInBackground(Void... voids) {
        Log.d("Task3/MessageGetter", "was called asynchronously");

        // try to connect until it works or we tried <i>tries</i> times
        ResponseObject responseObject = new ResponseObject(false, null);
        for(int i=0; i<tries; i++){
            Log.d("Task3/MessageGetter", "Establishing connection to "+this.serverIP+":"+this.serverPort+". Try "+(i+1)+" / "+tries);
            responseObject = register();
            if(responseObject.isSuccess()) break;
        }
        // packet is now in responseObject
        if(!responseObject.isSuccess()){
            return null;
        }

        // check if there was a server-side error. E.g. duplicate uuid or username
        // Is this a fault of the server??? // TODO: why must usernames be unique, but uuids not??
        /*
        1: Roger, 8566ba1e-5017-4b76-a473-e22d36b317ce
        2: server, 760cc36c-1894-4408-82c5-7e590e3b2932
        3: roger, 760cc36c-1894-4408-82c5-7e590e3b2932
        */
        // AND WHY DOES IT ACCEPT "null" as a UUID?!

        // get response as JSON
        JSONObject jsonObject;
        JSONObject headerObject;
        try {
            jsonObject = new JSONObject(new String(responseObject.getPacket().getData()).trim());
            headerObject = new JSONObject(jsonObject.getString("header"));
        } catch (JSONException e) {
            Log.e("Task3/MessageGetter", "Server responded with invalid JSON Object. Treating this as Failure. "+e.getMessage());
            e.printStackTrace();
            return null;
        }

        try {
            if(headerObject.getString("type").equals("error")){
                Log.d("Task3/MessageGetter", "Server responded with error. Check that username (and maybe uuid if you like) are unique");
                JSONObject bodyObject = new JSONObject(jsonObject.getString("body"));
                Log.d("Task3/MessageGetter", "\tError code: "+bodyObject.getString("content"));
                return null;
            }

            if(headerObject.get("type").equals("ack")){
                Log.d("Task3/MessageGetter", "Server responded with ACK.");
                return responseObject.getPacket();
            }

            // if the server responded with anything else, something is wrong
            Log.e("Task3/MessageGetter", "Server responend with an unexpected header type: "+headerObject.getString("type"));
            return null;
        } catch (JSONException e) {
            Log.e("Task3/MessageGetter", "Header is not as expected. Treating this as Failure. "+e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private ResponseObject register(){
        try {
            // Socket on a random port
            socket = new DatagramSocket();


            byte [] data = generateRegisterRequestString().getBytes(); // prepare request to send
            DatagramPacket packet = new DatagramPacket(data, data.length, this.serverIP, this.serverPort);

            socket.send(packet);
            Log.d("Task3/MessageGetter", "sent packet: "+new String(packet.getData()).trim());
            socket.setSoTimeout(this.timeout);

            // prepare packet to recieve answer
            packet.setData(new byte[RESPONSEPACKETSIZE]);

            socket.receive(packet); // blocking call

            Log.d("Task3/MessageGetter", "Received answer: "+ new String(packet.getData()).trim());
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
            Log.d("Task3/MessageGetter","Socket Timeout while waiting to receive ACK");
            return new ResponseObject(false,null);
        }
        catch (SocketException e) {
            Log.e("Task3/MessageGetter","Socket Exception: "+e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Task3/MessageGetter","I/O Exception: "+e.getMessage());
            e.printStackTrace();
        }
        return new ResponseObject(false,null);
    }

    private String generateRegisterRequestString(){

        return  "{\"header\":{" +
                "\"username\": \""+username+"\"," +
                "\"uuid\": \""+uuid+"\"," +
                "\"timestamp\": \"{}\"," +
                "\"type\": \"retrieve_chat_log\"" +
                "}," +
                "\"body\": {}" +
                "}";
    }

    private class ResponseObject {
        private boolean success;
        private DatagramPacket packet;

        ResponseObject(boolean s, DatagramPacket p){
            this.success = s;
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

    @Override
    protected void onPostExecute(DatagramPacket rp) {
        super.onPostExecute(rp);

        Boolean aBoolean = (rp == null);
        delegate.processFinish(rp);

        Log.d("Task3/MessageGetter", "finished successfully? : "+aBoolean.toString());
        MainActivity.uuid = this.uuid;
        MainActivity.username = this.username;
        if(aBoolean){

        } else {
            // what to do if failed to connect?
            Toast toast = Toast.makeText(context, "Failed to get Messages", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

}
