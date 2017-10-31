package ch.ethz.inf.vs.a3.vsminkerchat;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import ch.ethz.inf.vs.a3.message.ErrorCodes;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.*;

public class Task1Deregistrator extends AsyncTask<Void, Void, Boolean> {
    private final int tries;
    private final int serverPort;
    private String uuid;
    private Context appcontext;
    private InetAddress serverIP;
    private DatagramSocket socket;
    private int timeout = 2000; // miliseconds
    private int RESPONSEPACKETSIZE = 1000; // TODO: handle buffer overflow / what when not received all data?
    private String username;
    private String latest_errormessage;

    Task1Deregistrator(Context appcontext, String uuid, String username, String serverIP, int serverPort, int tries, int timeout){
        this.username = username;
        this.uuid = uuid;
        this.appcontext = appcontext;
        this.timeout = timeout;
        this.tries = tries;
        this.serverPort = serverPort;
        try {
            this.serverIP = InetAddress.getByName(serverIP); // validates IP format
        } catch (UnknownHostException e) { // If this happens, the app is wrong. DO NOT PASS BAD ADDRESSES
            Log.e("Task1/Deregistrator", "invalid IP Address");
            e.printStackTrace();
        }
    }

    Task1Deregistrator(Context appcontext){
        this(appcontext, MainActivity.uuid, MainActivity.username, "10.0.2.2",4446,5, 2000);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        Log.d("Task1/Deregistrator", "was called");
        ResponseObject responseObject = new ResponseObject(false, null);
        latest_errormessage = null;
        for(int i=0; i<tries; i++){
            Log.d("Task1/Deegistrator", "Establishing connection. Try "+(i+1)+" / "+tries);
            responseObject = deregister();
            if(responseObject.isSuccess()) break;
        }
        // packet is now in responseObject
        if(!responseObject.isSuccess()){
            return false;
        }
        
        // get response as JSON
        JSONObject jsonObject;
        JSONObject headerObject;
        try {
            jsonObject = new JSONObject(new String(responseObject.getPacket().getData()).trim());
            headerObject = new JSONObject(jsonObject.getString("header"));
        } catch (JSONException e) {
            Log.e("Task1/Deregistrator", "Server responded with invalid JSON Object. Treating this as Failure. "+e.getMessage());
            e.printStackTrace();
            return false;
        }

        try {
            if(headerObject.getString("type").equals("error")){
                Log.d("Task1/Deregistrator", "Server responded with error. Check that username (and maybe uuid if you like) are unique");
                JSONObject bodyObject = new JSONObject(jsonObject.getString("body"));
                Log.d("Task1/Deregistrator", "\tError code: "+bodyObject.getString("content"));
                latest_errormessage = ErrorCodes.getStringError(bodyObject.getInt("content"));
                return false;
            }

            if(headerObject.get("type").equals("ack")){
                Log.d("Task1/Deregistrator", "Server responded with ACK.");
                return true;
            }

            // if the server responded with anything else, something is wrong
            Log.e("Task1/Deregistrator", "Server responend with an unexpected header type: "+headerObject.getString("type"));
            return false;
        } catch (JSONException e) {
            Log.e("Task1/Deregistrator", "Header is not as expected. Treating this as Failure. "+e.getMessage());
            e.printStackTrace();
            return false;
        }

    }

    private Task1Deregistrator.ResponseObject deregister(){
        try {
            // Socket on a random port
            socket = new DatagramSocket();


            byte [] data = generateDeregisterRequestString().getBytes(); // prepare request to send
            DatagramPacket packet = new DatagramPacket(data, data.length, this.serverIP, this.serverPort);

            socket.send(packet);
            Log.d("Task1/Deregistrator", "sent packet: "+new String(packet.getData()).trim());
            socket.setSoTimeout(this.timeout);

            // prepare packet to recieve answer
            packet.setData(new byte[RESPONSEPACKETSIZE]);

            socket.receive(packet); // blocking call

            Log.d("Task1/Deregistrator", "Received answer: "+ new String(packet.getData()).trim());

            return new Task1Deregistrator.ResponseObject(true, packet);

        } catch (SocketTimeoutException e){
            Log.d("Task1/Deregistrator","Socket Timeout while waiting to receive deregistration ACK");
            return new Task1Deregistrator.ResponseObject(false,null);
        }
        catch (SocketException e) {
            Log.e("Task1/Deregistrator","Socket Exception: "+e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Task1/Deregistrator","I/O Exception: "+e.getMessage());
            e.printStackTrace();
        }
        return new Task1Deregistrator.ResponseObject(false,null);
    }

    private String generateDeregisterRequestString(){
        //<debug>// this.uuid = "267970af-76c7-4f11-818b-5ddf76ad16d2";
        return  "{\"header\":{" +
                "\"username\": \""+this.username+"\"," +
                "\"uuid\": \""+this.uuid +"\"," +
                "\"timestamp\": \"{}\"," +
                "\"type\": \"deregister\"" +
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

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        Log.d("Task1/Deregistrator", "finished deregistration successfully? : "+aBoolean.toString());
        if(aBoolean){
            Toast toast = Toast.makeText(appcontext, "Successfully Deregistered "+this.username, Toast.LENGTH_SHORT);
            toast.show();
        } else {
            if(latest_errormessage == null) {latest_errormessage = "Deregistration failed for unexpected reasons.";}
            Toast toast = Toast.makeText(appcontext,  latest_errormessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
