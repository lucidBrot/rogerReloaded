package ch.ethz.inf.vs.a1.minker.antitheft;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private SpikeMovementDetector spikeMovementDetector;
    public static int SENSOR_LINEAR = 0;
    public static int SENSOR_MINE = 1;
    public static int SENSOR_DEFAULT = SENSOR_LINEAR;
    public static Context appcontext;
    private boolean waschecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appcontext = getApplicationContext();

        final Intent intent = new Intent(this, AntiTheftService.class);

        // store what sensor to use
        SharedPreferences sp = getSharedPreferences(getString(R.string.sharedprefs), Context.MODE_PRIVATE);
        SharedPreferences.Editor sped = sp.edit();
        sped.putInt("sensor_type", SENSOR_DEFAULT);
        sped.apply();

        final CheckBox onoff = findViewById(R.id.checkBox);
        onoff.setChecked(false);

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

    // legacy: (unregister sensor on pause and reregister on resume of application)
    // not doing this anymore because it's running in a background service anyways
    protected void onPause() {
        super.onPause();
        /* ///legacy code
        sensorManager.unregisterListener(spikeMovementDetector);
         */
        waschecked =((CheckBox) findViewById(R.id.checkBox)).isChecked();
    }

    // register sensor again after application being resumed
    protected void onResume() {
        super.onResume();
        /* ///legacy code
        sensorManager.registerListener(spikeMovementDetector, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        toast("registered sensor on "+spikeMovementDetector.toString());
        Log.d("a", "registered sensor on "+spikeMovementDetector.toString());
        */
        ((CheckBox) findViewById(R.id.checkBox)).setChecked(waschecked);
    }

}
