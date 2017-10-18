package ch.ethz.inf.vs.a2.minker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class Task2Activity extends AppCompatActivity implements ch.ethz.inf.vs.a2.minker.sensor.SensorListener, View.OnClickListener {

    private XmlSensor xmlSensorM;
    private SoapSensor soapSensorL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task2);
        findViewById(R.id.t2_btnManual).setOnClickListener(this);
        findViewById(R.id.t2_btnLibrary).setOnClickListener(this);
        xmlSensorM = new XmlSensor();
        xmlSensorM.registerListener(this);

        xmlSensorM.getTemperature();

    }

    /**
     * Callback function for receiving sensor values.
     *
     * @param value A sensor value
     */
    @Override
    public void onReceiveSensorValue(double value) {
        Log.d("Task2/Activity", "received sensor value: "+value);
        String s = getString(R.string.t2_temperature) +" "+ String.valueOf(value);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.t2_btnManual:
                Log.d("Task2/onClick","clicked manual button");
                xmlSensorM.getTemperature();
                break;
            case R.id.t2_btnLibrary:
                soapSensorL.getTemperature();
                break;
        }
    }
}
