package ch.ethz.inf.vs.a1.minker.antitheft;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;
import ch.ethz.inf.vs.a1.minker.antitheft.movement_detector.SpikeMovementDetector;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private SpikeMovementDetector spikeMovementDetector;
    public static int SENSOR_LINEAR = 0;
    public static int SENSOR_MINE = 1;
    public static int SENSOR_DEFAULT = SENSOR_LINEAR;

    public static Context getAppcontext() {
        if(appcontext!=null){
        return appcontext;} else return null;
    }

    public static Context appcontext = null;
    private boolean waschecked;
    private boolean prefDisabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //load default preferences on first app launch
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        appcontext = getApplicationContext();

        final Intent intent = new Intent(this, AntiTheftService.class);

        // store what sensor to use
        SharedPreferences sp = getSharedPreferences(getString(R.string.sharedprefs), Context.MODE_PRIVATE);
        String getsensor = sp.getString(getString(R.string.key_SENSOR_LIST), "unset");
        Log.d("p","Default loaded from preferences is sensor: "+getsensor);
        if(getsensor.equals("unset")){
            // need to set preference if sensor not yet set
            SharedPreferences.Editor sped = sp.edit();
            sped.putString(getString(R.string.key_SENSOR_LIST), String.valueOf(SENSOR_DEFAULT));
            Log.d("f", "set sensor back to default.");
            sped.apply();
        } // else { // it's already set. great.

        final CheckBox onoff = findViewById(R.id.checkBox);
        onoff.setChecked(false);

        onoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox) view;
                if (checkBox.isChecked()){
                    Log.d("d", "starting service");
                    prefDisabled = true;
                    startService(intent);
                    checkBox.setText(R.string.on);
                } else {
                    Log.d("d", "stopping service");
                    prefDisabled = false;
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
                i.putExtra("prefEnabled", !prefDisabled);
                Log.d("p", "Opening Settings Activity: Current sensor is "+MainActivity.this.getSharedPreferences(getString(R.string.sharedprefs), Context.MODE_PRIVATE).getString(getString(R.string.key_SENSOR_LIST), "unset"));
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

    public static void debugMyPrefs(SharedPreferences sp){
        //DEBUG
        Map<String,?> keys = sp.getAll();

        for(Map.Entry<String,?> entry : keys.entrySet())
        {
            Log.d("q/map values", entry.getKey() + ": " + entry.getValue().toString());
            Log.d("q/data type", entry.getValue().getClass().toString());

            if ( entry.getValue().getClass().equals(String.class))
                Log.d("q/data type", "String");
            else if ( entry.getValue().getClass().equals(Integer.class))
                Log.d("q/data type", "Integer");
            else if ( entry.getValue().getClass().equals(Boolean.class))
                Log.d("q/data type", "boolean");

        }
        //ENDDEBUG
    }
}
