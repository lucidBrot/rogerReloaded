package ch.ethz.inf.vs.minker.vs_minker_antitheftalarm;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.PopupMenu;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private SpikeMovementDetector spikeMovementDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Intent intent = new Intent(this, AntiTheftService.class);
        startService(intent);


        final CheckBox onoff = findViewById(R.id.checkBox);
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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings_menu_item:
                Intent i = new Intent(this,SettingActivity.class);
                this.startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
