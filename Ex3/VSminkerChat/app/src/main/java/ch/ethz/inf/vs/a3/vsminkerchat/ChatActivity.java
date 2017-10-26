package ch.ethz.inf.vs.a3.vsminkerchat;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Should be started when Task1Registrator returns true
 */
public class ChatActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Log.d("Task1/ChatActivity", "starting Chatactivity");
    }

    @Override
    public void onBackPressed() {
        Task1Deregistrator deregistrator = new Task1Deregistrator(getApplicationContext(), MainActivity.uuid, MainActivity.username, MainActivity.serverIP, MainActivity.serverPort, 5, 2000);
        deregistrator.execute();
        Log.d("Task1/ChatActivity", "back button pressed. Starting deregistration asynchronically");
        super.onBackPressed();
    }

    // TODO: onPause and onResume - deregister and reregister?
}
