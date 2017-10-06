package ch.ethz.inf.vs.minker.vs_minker_antitheftalarm;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;
import ch.ethz.inf.vs.minker.vs_minker_antitheftalarm.AlarmCallback;

import java.io.Console;

import static android.content.Context.SENSOR_SERVICE;

public class SpikeMovementDetector extends AbstractMovementDetector {

    private Context activity;
    public SpikeMovementDetector(AlarmCallback callback, int sensitivity, Context context) {
        super(callback, sensitivity);
        this.activity = context;
    }

    @Override
    public boolean doAlarmLogic(float[] values) {
		// TODO, insert your logic here
        Log.d("a", "started doAlarmLogic");

        SensorManager sensorManager=(SensorManager) activity.getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        // testing callback
        callback.onDelayStarted();
        return false;
    }
}
