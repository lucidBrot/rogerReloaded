package ch.ethz.inf.vs.a2.minker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import ch.ethz.inf.vs.a2.minker.sensor.RawHttpSensor;
import ch.ethz.inf.vs.a2.minker.sensor.SensorListener;
import ch.ethz.inf.vs.a2.minker.sensor.TextSensor;

public class TemperatureSensorActivity extends AppCompatActivity implements SensorListener{

    private TextView sensorValuesTextViewRaw;
    private TextView sensorValuesTextViewTextURL;
    private TextView sensorValuesTextViewJSON;
    private RawHttpSensor myRawHttpSensor;
    private TextSensor myTextSensor;
    private JsonSensor myJSONSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature_sensor);
        setTitle(R.string.temperature_sensor);

        sensorValuesTextViewRaw = (TextView) findViewById(R.id.textViewRaw);
        sensorValuesTextViewTextURL = (TextView) findViewById(R.id.textViewTextURL);
        sensorValuesTextViewJSON = (TextView) findViewById(R.id.textViewJSON);


        myRawHttpSensor = new RawHttpSensor();
        myTextSensor = new TextSensor();
        myJSONSensor = new JsonSensor();
        /*
        myRawHttpSensor.registerListener(this);
        myRawHttpSensor.getTemperature();

        myTextSensor.registerListener(this);
        myTextSensor.getTemperature();
        */
        myJSONSensor.registerListener(this);
        myJSONSensor.getTemperature();
    }

    @Override
    public void onReceiveSensorValue(double value) {
        sensorValuesTextViewRaw.setText(String.valueOf(value));
    }

    @Override
    public void onReceiveMessage(String message) {
    }
}
