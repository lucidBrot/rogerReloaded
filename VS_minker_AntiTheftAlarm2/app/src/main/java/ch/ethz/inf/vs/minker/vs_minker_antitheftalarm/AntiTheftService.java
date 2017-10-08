package ch.ethz.inf.vs.minker.vs_minker_antitheftalarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class AntiTheftService extends Service implements AlarmCallback{

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private NotificationManager mgr;
    private int NOTIFICATION_ID = 101;
    private boolean stahp = false;
    public static final int DEFAULT_SENSITIVITY = 1;
    public static final float DEFAULT_DELAY = 5; // in seconds
    private float delay;
    private int sensitivity ;



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        SharedPreferences sp = getSharedPreferences(getString(R.string.sharedprefs), Context.MODE_PRIVATE);
        sensitivity = sp.getInt("sensitivity", DEFAULT_SENSITIVITY);
        delay = sp.getFloat("delay", DEFAULT_DELAY);
        Log.d("g", "set sensitivity "+sensitivity+" and delay "+delay+" seconds.");

        mgr = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        /* /// legacy code. keeping as reference
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        */

        sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
        SpikeMovementDetector spikeMovementDetector = new SpikeMovementDetector(this, sensitivity);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(spikeMovementDetector, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        Log.d("AntiTheftService","started service");
        return START_STICKY;
    }

    private void showNotification() {

        NotificationCompat.Builder note = new NotificationCompat.Builder(this);
        note.setContentTitle("Device Accelerometer Notification");
        note.setTicker("New Message Alert!");
        note.setAutoCancel(true);
        // to set default sound/light/vibrate or all
        note.setDefaults(Notification.DEFAULT_VIBRATE);
        // Icon to be set on Notification
        note.setSmallIcon(R.mipmap.ic_launcher);
        // This pending intent will open after notification click
        PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this,
                MainActivity.class), 0);
        // set pending intent to notification builder
        note.setContentIntent(pi);
        Log.d("AntiTheftService", "showing Notification");
        mgr.notify(NOTIFICATION_ID, note.build());
    }

    @Override
    public void onDestroy(){
        stahp = true; //
        mgr.cancel(NOTIFICATION_ID); //clean up current notifications

        Log.d("AntiTheftService", "onDestroy has been called for AntiTheftService");
    }

    @Override
    public void onDelayStarted() {
        if(!stahp) {
            Log.d("AntiTheftService", "onDelayStarted");
            // not sure why, but k I'll give you a delay
            /*Timer tim = new Timer();
            tim.schedule(new TimerTask() {
                @Override
                public void run() {
                    // this code will be executed after delay seconds
                    String did = (!stahp) ? "did" : "didn't";
                    if(!stahp) { showNotification(); }
                    Log.d("AntiTheftService", "Delay timer finished. "+did+" show notification.");
                }
            }, (long) (1000*delay));
            Log.d("AntiTheftService", "onDelayStarted finished");
            */

            if(!stahp) {showNotification();}
        }
    }
}
