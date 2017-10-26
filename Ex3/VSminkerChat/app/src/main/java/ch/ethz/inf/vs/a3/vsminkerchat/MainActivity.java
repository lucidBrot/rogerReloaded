package ch.ethz.inf.vs.a3.vsminkerchat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    static String uuid = null; // will be set by Task1Registrator and can then be reused for further communication
    static String username = null; // will be set by Task1Registrator and can then be reused for further communication
    static String serverIP = "10.0.2.2";
    static int serverPort = 4446;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // bind onClick listeners
        findViewById(R.id.btn_join).setOnClickListener(this);
        findViewById(R.id.btn_settings).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Log.d("MainActivity", "Captured some onClickEvent");
        switch(view.getId()){
            case R.id.btn_join: // try to register with server
                username = ((EditText) findViewById(R.id.et_username)).getText().toString();
                Task1Registrator t1r = new Task1Registrator(serverIP, serverPort, 5, 2000, username, this); // DO NOT PASS BAD IP ADDRESSES PLZ
                t1r.execute();
                break;
            case R.id.btn_settings: // start settings Activity
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void toast(String msg){
        Context context = getApplicationContext();
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.show();
    }
}
