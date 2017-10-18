package ch.ethz.inf.vs.a2.minker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

public class Task2Activity extends AppCompatActivity implements ch.ethz.inf.vs.a2.minker.sensor.SensorListener {

    private XmlSensor xmlSensor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task2);

        xmlSensor = new XmlSensor();
        xmlSensor.registerListener(this);

        xmlSensor.getTemperature();

    }

    /**
     * Callback function for receiving sensor values.
     *
     * @param value A sensor value
     */
    @Override
    public void onReceiveSensorValue(double value) {
        Log.d("Task2/Activity", "received sensor value: "+value);
        String s = getString(R.string.t2_temperature) + String.valueOf(value);
        ((TextView) findViewById(R.id.t2_temperature_display)).setText(s);
    }

    /**
     * Callback function for receiving messages.
     * Could be useful for debugging.
     *
     * @param message Message
     */
    @Override
    public void onReceiveMessage(String message) {

    }
}
