package ch.ethz.inf.vs.minker.vs_minker_antitheftalarm;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private SpikeMovementDetector spikeMovementDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int sensitivity = 100;
        sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);

        this.spikeMovementDetector = new SpikeMovementDetector(new AlarmCallback() {
            @Override
            public void onDelayStarted() {
                toast("Callback worked");
            }
        },
                sensitivity,
                this,
                sensorManager);

        Intent intent = new Intent(this, AntiTheftService.class);
        startService(intent);

    }

    private void toast(String msg){
        Context context = getApplicationContext();
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    // unregister sensor on pause and reregister on resume of application
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(spikeMovementDetector);
    }

    // register sensor again after application being resumed
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(spikeMovementDetector, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        toast("registered sensor on "+spikeMovementDetector.toString());
        Log.d("a", "registered sensor on "+spikeMovementDetector.toString());
    }
}
