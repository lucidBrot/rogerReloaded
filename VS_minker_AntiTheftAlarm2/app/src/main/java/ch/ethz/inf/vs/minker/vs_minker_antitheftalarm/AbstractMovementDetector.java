package ch.ethz.inf.vs.minker.vs_minker_antitheftalarm;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import android.hardware.SensorManager;
import android.util.Log;
import ch.ethz.inf.vs.minker.vs_minker_antitheftalarm.AlarmCallback;

import java.util.Arrays;

public abstract class AbstractMovementDetector implements SensorEventListener {

    protected AlarmCallback callback;
    protected int sensitivity;
    private double mAccelLast;
    private double mAccelCurrent;
    private boolean first = true;

    public AbstractMovementDetector(AlarmCallback callback, int sensitivity){
        this.callback = callback;
        this.sensitivity = sensitivity;
    }

    // Sensor monitoring
    @Override
    public void onSensorChanged(SensorEvent event) {

            // Copy values because the event is not owned by the application
            float[] values = event.values.clone();
            Log.d("e", "on Sensor Changed: " + Arrays.toString(values));

            // This was pre-coded but that doesn't trigger.
            /* if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                Log.d("f", "linear acceleration triggered");
                if (doAlarmLogic(values)) {
                    callback.onDelayStarted();
                }
            } */
            // TODO: register the linear acceleration listener

            // own implementation
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
            double diff = mAccelCurrent - mAccelLast;
            if (diff > sensitivity) {
                Log.d("f", "noticed acceleration above threshhold: " + diff);
                if (!first) { callback.onDelayStarted(); }
                else {
                    Log.d("f", "skipped first occurrence");
                    first = false;
                }
            }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do not do anything
    }

    public abstract boolean doAlarmLogic(float[] values);

}
