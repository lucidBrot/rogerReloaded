package ch.ethz.inf.vs.a2.minker;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class RESTServerActivity extends AppCompatActivity {

    private Button serverButton;
    private EditText portField;

    private AlertDialog.Builder invalidPort;

    private int port;
    private final static String TAG = "RESTApp";
    private TextView ipText;

    private boolean isServiceBound = false;

    private ListView logList;

    private RESTServerService service;
    private Intent serviceIntent;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            RESTServerService.LocalBinder binder = (RESTServerService.LocalBinder) iBinder;
            service = binder.getService();
            if(service != null) {
                service.setActivity(RESTServerActivity.this);
                if (service.isRunning()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            serverButton.setText(R.string.stop_service_button);
                            portField.setEnabled(false);
                            logList.setAdapter(service.logAdapter);
                        }
                    });
                }
            }
            isServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isServiceBound = false;
            service.setActivity(null);
        }
    };

    public void scrollToBottom(){
        logList.post(new Runnable() {
            @Override
            public void run() {
                logList.setSelection(logList.getAdapter().getCount() -1);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restserver);

        serverButton = (Button) findViewById(R.id.serverButton);

        portField = (EditText) findViewById(R.id.port);
        ipText = (TextView) findViewById(R.id.ip_text);
        if(savedInstanceState != null){
            portField.setText(savedInstanceState.getString("port"));
        }
        logList = (ListView) findViewById(R.id.log_list);

        String ipAddress = Util.getIPAddress(true);
        if(ipAddress.equals("")){
            ipAddress = Util.getIPAddress(false);
            ipText.setTextSize(18);
        }
        ipText.setText(ipAddress);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            invalidPort = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert);
        } else {
            invalidPort = new AlertDialog.Builder(this);
        }
        invalidPort.setTitle("Invalid Port")
                .setMessage("Your input was invalid. The port has to be a number between 1025 and 65535")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
        serviceIntent = new Intent(this,RESTServerService.class);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("port", portField.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        startService(serviceIntent);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        super.onResume();
    }

    public void serverControl(View view) {
        if (service.isRunning()) {
            service.stopService();
            serverButton.setText(R.string.start_service_button);
            portField.setEnabled(true);
        } else {

            try {
                int userPort = Integer.parseInt(portField.getText().toString());
                if (userPort > 1024 && userPort <= 65535) {
                    port = userPort;
                } else {
                    invalidPort.show();
                    return;
                }
            } catch (Exception e) {
                invalidPort.show();
                return;
            }
            service.startService(port);
            if(logList.getAdapter() == null){
                logList.setAdapter(service.logAdapter);
            }

            serverButton.setText(R.string.stop_service_button);
            portField.setEnabled(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isServiceBound) {
            unbindService(serviceConnection);
        }
        if(isFinishing()){
            stopService(serviceIntent);
        }
    }
}
