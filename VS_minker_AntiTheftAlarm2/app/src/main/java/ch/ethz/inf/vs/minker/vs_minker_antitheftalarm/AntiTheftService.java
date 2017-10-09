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
    private NotificationManager silent_mgr; // might not be neccessary but I'm not sure if mgr stores its defaults.
    private SpikeMovementDetector spikeMovementDetector;
    private int NOTIFICATION_ID = 101;
    private int NOTIFICATION_ID_SILENT = 102;
    private boolean stahp = false;
    public static final int DEFAULT_SENSITIVITY = 1;
    public static final float DEFAULT_DELAY = 5; // in seconds
    private float delay;
    private int sensitivity ;
    private int phone_taken = 0; // 1 means timer is running, 2 means timer is done and we can alarm and 0 means base state



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        silent_mgr = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        showSilentNotification();

        SharedPreferences sp = getSharedPreferences(getString(R.string.sharedprefs), Context.MODE_PRIVATE);
        sensitivity = sp.getInt("sensitivity", DEFAULT_SENSITIVITY);
        delay = sp.getFloat("delay", DEFAULT_DELAY);
        Log.d("g", "set sensitivity "+sensitivity+" and delay "+delay+" seconds.");

        mgr = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        // only register the sensor we need
        sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
        spikeMovementDetector = new SpikeMovementDetector(this, sensitivity);
        if (sp.getInt("sensor_type", MainActivity.SENSOR_DEFAULT) == MainActivity.SENSOR_MINE ) {
            Log.d("AntiTheftService", "setting up MINE sensor");
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        else if (sp.getInt("sensor_type", MainActivity.SENSOR_DEFAULT) == MainActivity.SENSOR_LINEAR){
            Log.d("AntiTheftService", "setting up LINEAR sensor");
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        }
        else {
            Log.e("AntiTheftService", "wrong sensor in settings! defaulting to MainActivity.SENSOR_MINE");
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        if ( accelerometer == null){
            Log.e("AntiTheftService", "Sensor is actually null. Device doesn't have that sensor.");
        } else {
            sensorManager.registerListener(spikeMovementDetector, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }

        Log.d("AntiTheftService","started service");
        return START_STICKY;
    }

    private void showNotification() { //TODO: keep sound consistently on in addition to the notification

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

    private void showSilentNotification() {
        NotificationCompat.Builder note = new NotificationCompat.Builder(this);
        note.setContentTitle("Anti Theft Service running");
        note.setTicker("Anti Theft Service running");
        note.setAutoCancel(false);
        // to set default sound/light/vibrate or all
        note.setDefaults(Notification.DEFAULT_LIGHTS);
        note.setVibrate(null); //don't vibrate
        // Icon to be set on Notification
        note.setSmallIcon(R.mipmap.ic_launcher);
        // This pending intent will open after notification click
        PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this,
                MainActivity.class), 0);
        // set pending intent to notification builder
        note.setContentIntent(pi);
        Log.d("AntiTheftService", "showing silent Notification");
        silent_mgr.notify(NOTIFICATION_ID_SILENT, note.build());
    }

    @Override
    public void onDestroy(){
        stahp = true;
        mgr.cancel(NOTIFICATION_ID); //clean up current notifications
        sensorManager.unregisterListener(spikeMovementDetector);
        silent_mgr.cancel(NOTIFICATION_ID_SILENT);
        Log.d("AntiTheftService", "onDestroy has been called for AntiTheftService. Unregistered listener.");
    }

    @Override
    public void onDelayStarted() {

        if(phone_taken == 0) { // if the protection was never triggered before, wait 5 sec.
            (new Timer()).schedule(new TimerTask() {
                @Override
                public void run() {
                    if(!stahp) { showNotification();}
                    phone_taken = 2;
                    Log.d("AntiTheftService", "timer ran out!");
                }
            }, (long) (1000*delay));
        } else if (phone_taken == 1) { // timer is running, do nothing as the alarm will triger anyways
        } else { // timer is over, do usual alarming unless service stopped
            if (!stahp) {
                Log.d("AntiTheftService", "onDelayStarted");
                if (!stahp) {
                    showNotification();
                }
            }
        }
    }
}
