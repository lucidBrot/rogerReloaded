package ch.ethz.inf.vs.a2.minker.webservices;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class Task2Activity extends AppCompatActivity implements ch.ethz.inf.vs.a2.sensor.SensorListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task2);
    }

    /**
     * Callback function for receiving sensor values.
     *
     * @param value A sensor value
     */
    @Override
    public void onReceiveSensorValue(double value) {

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
