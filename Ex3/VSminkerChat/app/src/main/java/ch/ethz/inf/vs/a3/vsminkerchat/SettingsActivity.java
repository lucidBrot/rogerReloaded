package ch.ethz.inf.vs.a3.vsminkerchat;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ((Button) findViewById(R.id.btn_save_settings)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    MainActivity.serverIP = ((EditText) findViewById(R.id.et_server_ip)).getText().toString();
                    MainActivity.serverPort = Integer.valueOf(((EditText) findViewById(R.id.et_server_port)).getText().toString());
                    finish();
                } catch (Exception e){
                    Log.d("Task1/SettingsActivity", "Invalid Settings. Might have partially applied them now: "+e.getMessage());
                    toast("Bad settings. Please fix and retry");
                    // settings cannot be changed during connection either, because then the ChatActivity should be open, which does not allow to open settings
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    private void toast(String msg){
        Context context = getApplicationContext();
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((EditText) findViewById(R.id.et_server_port)).setText(String.valueOf(MainActivity.serverPort));
        ((EditText) findViewById(R.id.et_server_ip)).setText(MainActivity.serverIP);
    }
}
