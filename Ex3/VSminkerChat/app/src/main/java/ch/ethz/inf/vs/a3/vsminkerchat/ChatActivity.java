package ch.ethz.inf.vs.a3.vsminkerchat;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;

import ch.ethz.inf.vs.a3.clock.VectorClock;
import ch.ethz.inf.vs.a3.clock.VectorClockComparator;
import ch.ethz.inf.vs.a3.message.Message;
import ch.ethz.inf.vs.a3.message.MessageComparator;
import ch.ethz.inf.vs.a3.message.MessageTypes;
import ch.ethz.inf.vs.a3.queue.PriorityQueue;

/**
 * Should be started when Task1Registrator returns true
 */
public class ChatActivity extends AppCompatActivity implements AsyncResponse {
    private int tries;
    private String uuid; // random identifier
    private int serverPort;
    private String serverIP;
    private String username;
    private TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Log.d("Task1/ChatActivity", "starting Chatactivity");
        serverPort = MainActivity.serverPort;
        uuid = MainActivity.uuid;
        username = MainActivity.username;
        serverIP = MainActivity.serverIP;

        textView = findViewById(R.id.textView2);


    }

    @Override
    public void onBackPressed() {
        Task1Deregistrator deregistrator = new Task1Deregistrator(getApplicationContext(), MainActivity.uuid, MainActivity.username, MainActivity.serverIP, MainActivity.serverPort, 5, 2000);
        deregistrator.execute();
        Log.d("Task1/ChatActivity", "back button pressed. Starting deregistration asynchronically");
        super.onBackPressed();
    }

    public void onMessageUpdate(View view){
        //"retrieve_chat_log"
        Task3MessageGetter t3m = new Task3MessageGetter(serverIP, serverPort, 5, 2000, username, this); // DO NOT PASS BAD IP ADDRESSES PLZ
        t3m.delegate = this;
        t3m.execute();
    }

    @Override
    public void processFinish(ArrayList<DatagramPacket> myPackets) {

        ArrayList<Message> messages = new ArrayList<>();
        for (DatagramPacket myPacket : myPackets){
            try {
                JSONObject jsonObject = new JSONObject(new String(myPacket.getData()).trim());
                JSONObject headerObject = new JSONObject(jsonObject.getString("header"));
                JSONObject bodyObject = new JSONObject(jsonObject.getString("body"));

                String messageType = headerObject.getString("type");
                if (messageType.equals(MessageTypes.CHAT_MESSAGE)){
                    String timestamp = headerObject.getString("timestamp");
                    String label = bodyObject.getString("content");

                    messages.add(new Message(timestamp,label));
                }

            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }


        PriorityQueue<Message> priorityQueue = new PriorityQueue<>(new MessageComparator());
        for(Message myMessage:messages){
            priorityQueue.add(myMessage);
        }

        //EditTextView
        String textviewString = "";
        while (priorityQueue.size() != 0)
        {
            Message message = priorityQueue.poll();
            textviewString += message.getLabel() + "\n";
        }
        textView.setText(textviewString);


    }


    // TODO: onPause and onResume - deregister and reregister?
}
