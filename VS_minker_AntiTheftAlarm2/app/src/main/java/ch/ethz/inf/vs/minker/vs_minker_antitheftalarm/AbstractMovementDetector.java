package ch.ethz.inf.vs.minker.vs_minker_antitheftalarm;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import android.hardware.SensorManager;
import android.util.Log;
import ch.ethz.inf.vs.minker.vs_minker_antitheftalarm.AlarmCallback;

public abstract class AbstractMovementDetector implements SensorEventListener {

    protected AlarmCallback callback;
    protected int sensitivity;

    public AbstractMovementDetector(AlarmCallback callback, int sensitivity, Context context, SensorManager sensManager){
        this.callback = callback;
        this.sensitivity = sensitivity;
    }

    // Sensor monitoring
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            Log.d("sensor", "linear acceleration triggered");
            // Copy values because the event is not owned by the application
            float[] values = event.values.clone();
            if(doAlarmLogic(values)){
                callback.onDelayStarted();
            }
        }

        Log.d("a", "Sensor triggered: "+event.sensor.getStringType());

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            Log.d("b", "Sensor triggered: "+event.sensor.getStringType());
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do not do anything
    }

    public abstract boolean doAlarmLogic(float[] values);

}
