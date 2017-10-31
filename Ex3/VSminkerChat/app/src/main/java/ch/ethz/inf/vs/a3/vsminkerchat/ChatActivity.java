package ch.ethz.inf.vs.a3.vsminkerchat;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Should be started when Task1Registrator returns true
 */
public class ChatActivity extends AppCompatActivity implements AsyncResponse {
    private int tries;
    private String uuid; // random identifier
    private int serverPort;
    private String serverIP;
    private String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Log.d("Task1/ChatActivity", "starting Chatactivity");
        serverPort = MainActivity.serverPort;
        uuid = MainActivity.uuid;
        username = MainActivity.username;
        serverIP = MainActivity.serverIP;


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
    public void processFinish(DatagramPacket myPacket) {

    }


    // TODO: onPause and onResume - deregister and reregister?
}
