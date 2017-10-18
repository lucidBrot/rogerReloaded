package ch.ethz.inf.vs.a2.minker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import ch.ethz.inf.vs.a2.minker.sensor.AbstractSensor;

public class Task2Activity extends AppCompatActivity implements ch.ethz.inf.vs.a2.minker.sensor.SensorListener, View.OnClickListener {

    private XmlSensor xmlSensorM;
    private SoapSensor soapSensorL;
    private boolean currSensorIsM;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task2);
        findViewById(R.id.t2_btnManual).setOnClickListener(this);
        findViewById(R.id.t2_btnLibrary).setOnClickListener(this);
        xmlSensorM = new XmlSensor();
        xmlSensorM.registerListener(this);
        soapSensorL = new SoapSensor();
        // soapSensorL.registerListener(this);
        currSensorIsM = true;

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
                // un- and reregistering is not neccessary, but in case some sensor were to send messages - I don't care about them if I use the other one
                if(!currSensorIsM){
                    soapSensorL.unregisterListener(this);
                    xmlSensorM.registerListener(this);
                    currSensorIsM = true;
                }
                xmlSensorM.getTemperature();
                break;
            case R.id.t2_btnLibrary:
                if(currSensorIsM){
                    xmlSensorM.unregisterListener(this);
                    soapSensorL.registerListener(this);
                    currSensorIsM = false;
                }
                soapSensorL.getTemperature();
                break;
        }
    }
}
