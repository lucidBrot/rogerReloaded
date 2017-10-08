package ch.ethz.inf.vs.minker.vs_minker_antitheftalarm;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private SpikeMovementDetector spikeMovementDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* /// legacy code before I used the Service
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
        */

        final Intent intent = new Intent(this, AntiTheftService.class);
        startService(intent);


        CheckBox onoff = findViewById(R.id.checkBox);
        onoff.setChecked(true);

        onoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox) view;
                if (checkBox.isChecked()){
                    Log.d("d", "starting service");
                    startService(intent);
                    checkBox.setText(R.string.on);
                } else {
                    Log.d("d", "stopping service");
                    stopService(intent);
                    checkBox.setText(R.string.off);
                }
            }
        });
        //stopService(intent);
    }

    private void toast(String msg){
        Context context = getApplicationContext();
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    // unregister sensor on pause and reregister on resume of application
    protected void onPause() {
        super.onPause();
        /* ///legacy code
        sensorManager.unregisterListener(spikeMovementDetector);
         */
    }

    // register sensor again after application being resumed
    protected void onResume() {
        super.onResume();
        /* ///legacy code
        sensorManager.registerListener(spikeMovementDetector, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        toast("registered sensor on "+spikeMovementDetector.toString());
        Log.d("a", "registered sensor on "+spikeMovementDetector.toString());
        */
    }
}
