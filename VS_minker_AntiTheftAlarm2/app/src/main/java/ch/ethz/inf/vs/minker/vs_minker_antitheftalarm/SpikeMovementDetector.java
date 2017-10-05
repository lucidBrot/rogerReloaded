package ch.ethz.inf.vs.minker.vs_minker_antitheftalarm;

import android.util.Log;
import ch.ethz.inf.vs.minker.vs_minker_antitheftalarm.AlarmCallback;

import java.io.Console;

public class SpikeMovementDetector extends AbstractMovementDetector {

    public SpikeMovementDetector(AlarmCallback callback, int sensitivity) {
        super(callback, sensitivity);
    }

    @Override
    public boolean doAlarmLogic(float[] values) {
		// TODO, insert your logic here
        Log.d("a", "started doAlarmLogic");
        // testing callback
        callback.onDelayStarted();
        return false;
    }
}
