package ch.ethz.inf.vs.a2.minker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import ch.ethz.inf.vs.a2.minker.sensor.RawHttpSensor;
import ch.ethz.inf.vs.a2.minker.sensor.SensorListener;
import ch.ethz.inf.vs.a2.minker.sensor.TextSensor;

public class TemperatureSensorActivity extends AppCompatActivity implements SensorListener,AdapterView.OnItemSelectedListener {

    private TextView sensorValuesTextView;
    private Spinner sensorTypeSpinner;

    private RawHttpSensor myRawHttpSensor;
    private TextSensor myTextSensor;
    private JsonSensor myJSONSensor;
    private CharSequence sensorTextTemp;

    int itemSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature_sensor);
        setTitle(R.string.temperature_sensor);

        itemSelected = 0;

        sensorValuesTextView = (TextView) findViewById(R.id.textView);
        sensorTypeSpinner = (Spinner) findViewById(R.id.spinner);

        String[] sensorStringList = {"RawHttpSensor","TextSensor","JsonSensor"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sensorStringList);

        sensorTypeSpinner.setAdapter(adapter);
        sensorTypeSpinner.setOnItemSelectedListener(this);


        myRawHttpSensor = new RawHttpSensor();
        myTextSensor = new TextSensor();
        myJSONSensor = new JsonSensor();

        //init with rawHttpSensor, becasue it's the first item in the list and the default selected one
        myRawHttpSensor.registerListener(this);
        myRawHttpSensor.getTemperature();

    }

    public void refresh(View view){
        switch (itemSelected){
            case 0:
                myRawHttpSensor.getTemperature();
                break;
            case 1:
                myTextSensor.getTemperature();
                break;
            case 2:
                myJSONSensor.getTemperature();
                break;
        }
    }

    @Override
    public void onReceiveSensorValue(double value) {
        String sensor = "";
        switch (itemSelected){
            case 0:
                sensor += "Sensor: RawHttpSensor, Value: ";
                break;
            case 1:
                sensor += "Sensor: TextSensor, Value: ";
                break;
            case 2:
                sensor += "Sensor: JsonSensor, Value: ";
                break;
        }
        sensorValuesTextView.setText(sensor + String.valueOf(value) + String.valueOf(itemSelected));

    }

    @Override
    public void onReceiveMessage(String message) {
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i){
            case 0:
                //unregister all
                myRawHttpSensor.unregisterListener(this);
                myTextSensor.unregisterListener(this);
                myJSONSensor.unregisterListener(this);
                //reregister the new one
                myRawHttpSensor.registerListener(this);

                break;
            case 1:
                //unregister all
                myRawHttpSensor.unregisterListener(this);
                myTextSensor.unregisterListener(this);
                myJSONSensor.unregisterListener(this);
                //reregister the new one
                myTextSensor.registerListener(this);

                break;
            case 2:
                //unregister all
                myRawHttpSensor.unregisterListener(this);
                myTextSensor.unregisterListener(this);
                myJSONSensor.unregisterListener(this);
                //reregister the new one
                myJSONSensor.registerListener(this);
                break;
        }
        itemSelected = i;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

}
