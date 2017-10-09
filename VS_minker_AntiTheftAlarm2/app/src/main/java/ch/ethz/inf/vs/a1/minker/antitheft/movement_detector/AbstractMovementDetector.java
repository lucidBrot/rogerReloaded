package ch.ethz.inf.vs.a1.minker.antitheft.movement_detector;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import android.util.Log;
import ch.ethz.inf.vs.a1.minker.antitheft.AlarmCallback;

import java.util.Arrays;

public abstract class AbstractMovementDetector implements SensorEventListener {

    protected AlarmCallback callback;
    protected int sensitivity;


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

            if(doAlarmLogic(event.values)){
                callback.onDelayStarted();
            }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do not do anything
    }

    public abstract boolean doAlarmLogic(float[] values);

}
