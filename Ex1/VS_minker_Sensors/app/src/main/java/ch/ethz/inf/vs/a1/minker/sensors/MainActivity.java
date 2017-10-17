package ch.ethz.inf.vs.a1.minker.sensors;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static SensorManager sensorMgr;
    public static SensorManager getSensorMgr(){return sensorMgr;}
    private static List<Sensor> sensors;
    public static Sensor getSensor(int i){
        if(sensors != null){
            return sensors.get(i);
        }
        return null;
    }

    private int activeSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensors = sensorMgr.getSensorList(Sensor.TYPE_ALL);

        ArrayList<String> sensorStringList = new ArrayList<>();

        for (Sensor currSens:sensors) {

            sensorStringList.add(currSens.getName().toString());
        }

        ListView listView = (ListView)findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sensorStringList);
        listView.setAdapter(adapter);

        listView.setClickable(true);
        listView.setOnItemClickListener(myClickListener);
    }

    public AdapterView.OnItemClickListener myClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            activeSensor = i;
            startActivity();
        }
    };

    public void startActivity() {
        //code to be written to handle the click event
        Intent SensorActivity = new Intent(this, SensorActivity.class);
        SensorActivity.putExtra("SensorIndex", activeSensor);
        this.startActivity(SensorActivity);
    }
}
