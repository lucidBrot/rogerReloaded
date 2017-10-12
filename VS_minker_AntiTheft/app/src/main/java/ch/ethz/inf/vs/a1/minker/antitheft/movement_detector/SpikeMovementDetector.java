package ch.ethz.inf.vs.a1.minker.antitheft.movement_detector;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import ch.ethz.inf.vs.a1.minker.antitheft.AlarmCallback;
import ch.ethz.inf.vs.a1.minker.antitheft.MainActivity;
import ch.ethz.inf.vs.a1.minker.antitheft.R;

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

        try { // if real

            SharedPreferences sp = MainActivity.appcontext.getSharedPreferences(MainActivity.appcontext.getString(R.string.sharedprefs), Context.MODE_PRIVATE);
            int sensor = Integer.valueOf(sp.getString(MainActivity.appcontext.getString(R.string.key_SENSOR_LIST), String.valueOf(MainActivity.SENSOR_DEFAULT)));
            sensor = Integer.parseInt(sp.getString(MainActivity.appcontext.getString(R.string.key_SENSOR_LIST), "-1"));
            if (sensor == -1) {
                Log.d("f", "why is this shit not set!?");
            } // else {Log.d("f", "doAlarmLogic: sensor is "+String.valueOf(sensor));}
            if (sensor == MainActivity.SENSOR_MINE) {

                // own sensor implementation
                float x = values[0];
                float z = values[2];
                float y = values[1];
                mAccelLast = mAccelCurrent;
                mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
                double diff = mAccelCurrent - mAccelLast;
                if (diff > sensitivity) {
                    Log.d("f", "MINE: noticed acceleration above threshhold: " + diff);
                    if (!first) {
                        return true;
                    } else {
                        Log.d("f", "skipped first occurrence");
                        first = false;
                        return false;
                    }
                }
                return false;
            } else if (sensor == MainActivity.SENSOR_LINEAR) {
                // LINEAR sensor (theirs)
                float sum = 0;
                for (int i = 0; i < 3; i++) {
                    sum += values[i]*(values[i]>0?1:-1);
                }
                if (sum >= sensitivity) {
                    Log.d("f", "LINEAR: noticed acceleration above threshhold: " + sum);
                    return true;
                } else {
                    Log.d("f", "LINEAR: sum is " + sum);
                    return false;
                }

            } else {
                Log.e("SpikeMovementDetector", "Wrong sensor type specified. Defaulting to MINE");

                // own sensor implementation
                float x = values[0];
                float z = values[2];
                float y = values[1];
                mAccelLast = mAccelCurrent;
                mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
                double diff = mAccelCurrent - mAccelLast;
                if (diff > sensitivity) {
                    Log.d("f", "WRONG(MINE): noticed acceleration above threshhold: " + diff);
                    if (!first) {
                        return true;
                    } else {
                        Log.d("f", "skipped first occurrence");
                        first = false;
                        return false;
                    }
                }
                return false;
            }
        } catch (NullPointerException n) { // if testing, just use LINEAR
            Log.d("TESTING", "We're in the testing area. intended?");
            float sum = 0;
            for (int i = 0; i < 3; i++) {
                sum += values[i]*(values[i]>0?1:-1);
            }
            if (sum >= sensitivity) {
                Log.d("f", "LINEAR: noticed acceleration above threshhold: " + sum);
                return true;
            } else {
                Log.d("f", "LINEAR: sum is " + sum);
                return false;
            }
        }
    }
}
