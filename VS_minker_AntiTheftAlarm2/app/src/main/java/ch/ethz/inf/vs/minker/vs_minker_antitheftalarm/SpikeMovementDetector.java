package ch.ethz.inf.vs.minker.vs_minker_antitheftalarm;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;
import ch.ethz.inf.vs.minker.vs_minker_antitheftalarm.AlarmCallback;

import java.io.Console;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.SENSOR_SERVICE;

public class SpikeMovementDetector extends AbstractMovementDetector {


    public SpikeMovementDetector(AlarmCallback callback, int sensitivity) {
        super(callback, sensitivity);
        Log.d("a", "created SpikeMovementDetector");

    }

    @Override
    public boolean doAlarmLogic(float[] values) { // return true if device is being stolen
        // TODO: what logic?

        Log.d("a", "started doAlarmLogic");
        return true;
    }
}
