package ch.ethz.inf.vs.a1.minker.antitheft;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

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
    public static final float DEFAULT_DELAY = 2; // in seconds
    private float delay;
    private int sensitivity ;
    private int phone_taken = 0; // 1 means timer is running, 2 means timer is done and we can alarm and 0 means base state
    private MediaPlayer mediaPlayer_alarm;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        silent_mgr = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        showSilentNotification();

        mediaPlayer_alarm = MediaPlayer.create(MainActivity.appcontext, R.raw.alarm);

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
            Log.e("AntiTheftService", "Sensor is actually null. Device doesn't have that sensor. Using MINE instead");
            sp.edit().putInt("sensor_type", MainActivity.SENSOR_MINE).apply();
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        sensorManager.registerListener(spikeMovementDetector, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        Log.d("AntiTheftService","started service");
        return START_STICKY;
    }

    private void showNotification() { //TODO: keep sound consistently on

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

    /**
     * @param start pass True if this is the first time and the file should be played. If start is False, the Alarm will stop
     */
    private void playOrStopAlarm(boolean start){
        /* /// not working
        Intent intent = new Intent(MainActivity.appcontext, AlarmPlayerService.class);
        intent.putExtra("start", start);
        startService(intent);
        Log.d("AntiTheftService", "started Alarm service"); */

        Log.d("AntiTheftService", "started playing Alarm Music");
        if(start) {
            if(!mediaPlayer_alarm.isPlaying()) {
                mediaPlayer_alarm.setLooping(true);
                    AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
                    // set the volume of played media to maximum.
                    audioManager.setStreamVolume (AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),0);
                mediaPlayer_alarm.start();
            }
        } else {
            mediaPlayer_alarm.stop();
            mediaPlayer_alarm.release();
            Log.d("AntiTheftService", "stopped playing Alarm Music");
        }
    }

    @Override
    public void onDestroy(){
        stahp = true;
        playOrStopAlarm(false); // stop alarm if playing
        mgr.cancel(NOTIFICATION_ID); //clean up current notifications
        sensorManager.unregisterListener(spikeMovementDetector);
        silent_mgr.cancel(NOTIFICATION_ID_SILENT);
        Log.d("AntiTheftService", "onDestroy has been called for AntiTheftService. Unregistered listener.");
    }

    @Override
    public void onDelayStarted() {

        if(phone_taken == 0) { // if the protection was never triggered before, wait 5 sec.
            phone_taken = 1;
            (new Timer()).schedule(new TimerTask() {
                @Override
                public void run() {
                    if(!stahp) { Log.d("AntiTheftService", "timer ran first time"); playOrStopAlarm(true);}
                    phone_taken = 2;
                    Log.d("AntiTheftService", "timer ran out!");
                }
            }, (long) (1000*delay));
        } else if (phone_taken == 1) { // timer is running, do nothing as the alarm will trigger anyways
        } else { // timer is over, do usual alarming unless service stopped
            if (!stahp) {
                Log.d("AntiTheftService", "onDelayStarted");
                if (!stahp) {
                    // continue alarming
                }
            }
        }
    }
}
