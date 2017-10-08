package ch.ethz.inf.vs.minker.vs_minker_antitheftalarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

public class AntiTheftService extends Service implements AlarmCallback{

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    private NotificationManager mgr;
    private int NOTIFICATION_ID = 101;
    private boolean stahp = false;
    private int sensitivity = 100; // TODO: let the user set the sensitivity

    public AntiTheftService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
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

        Log.d("c","started service");
        Log.d("d", "started service (D)");
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
        Log.d("c", "showing Notification");
        mgr.notify(NOTIFICATION_ID, note.build());
    }

    @Override
    public void onDestroy(){
        stahp = true;
        mgr.cancel(NOTIFICATION_ID); //clean up notifications

        Log.d("d", "onDestroy has been called for AntiTheftService");
    }

    @Override
    public void onDelayStarted() {
        if(!stahp) {
            /* ///legacy cde. does not work with new structure but keeping as reference
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float diff = mAccelCurrent - mAccelLast;

            Log.d("c", "Sensor triggered: " + event.sensor.getStringType() + " with values\n\t x: " + x + "\t y:" + y + "\t z: " + z);
            */
            if (mAccel > -1) {
                showNotification();
            }
        }
    }
}
