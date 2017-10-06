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
    private SensorManager sensorManager;

    public SpikeMovementDetector(AlarmCallback callback, int sensitivity, Context context, SensorManager sensManager) {
        super(callback, sensitivity, context, sensManager);
        this.activity = context;
        this.sensorManager = sensManager;
    }

    @Override
    public boolean doAlarmLogic(float[] values) {
		// TODO, insert your logic here
        Log.d("a", "started doAlarmLogic");

        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        // calback after listener is triggered is called in AbstractMovementDetector.java

        return false;
    }
}
