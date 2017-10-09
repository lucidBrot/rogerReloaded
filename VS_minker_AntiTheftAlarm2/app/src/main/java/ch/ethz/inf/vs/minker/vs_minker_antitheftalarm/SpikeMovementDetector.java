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

    private double mAccelLast;
    private double mAccelCurrent;
    private boolean first = true;

    public SpikeMovementDetector(AlarmCallback callback, int sensitivity) {
        super(callback, sensitivity);
        Log.d("a", "created SpikeMovementDetector");

    }

    @Override
    public boolean doAlarmLogic(float[] values) { // return true if device is being stolen
        // TODO: what logic?

        // TODO: register the linear acceleration listener

        // own sensor implementation
        float x = values[0];
        float z = values[2];
        float y = values[1];
        mAccelLast = mAccelCurrent;
        mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
        double diff = mAccelCurrent - mAccelLast;
        if (diff > sensitivity) {
            Log.d("f", "noticed acceleration above threshhold: " + diff);
            if (!first) { return true; }
            else {
                Log.d("f", "skipped first occurrence");
                first = false;
                return false;
            }
        }
        return false;
    }
}
