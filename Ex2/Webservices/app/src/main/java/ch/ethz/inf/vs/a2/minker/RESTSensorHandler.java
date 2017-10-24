package ch.ethz.inf.vs.a2.minker;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by Josua on 10/24/17.
 */

public class RESTSensorHandler implements IRESTMappedHandler, SensorEventListener {
    private android.hardware.Sensor handledSensor ;
    private int numberValues;
    private String unitname;
    private float[] currentValues;

    public RESTSensorHandler(android.hardware.Sensor sensor, int numberOfValues, String unitName){
        handledSensor = sensor;
        numberValues = numberOfValues;
        this.unitname = unitName;
    }

    @Override
    public String handle(Map<String, String> request, Map<String, String> header, Map<String, String> param) {
        String head = "<head><title>" + handledSensor.getName() + "</title></head>";
        String body = "<body><h1>" + handledSensor.getName() + "</h1><p>Current Values: " + Arrays.toString(currentValues) + "</p>" +
                "<p><a href=\"/sensors\">Back</a></p></body>";
        return "<html>" + head + body + "</html>";
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        currentValues = sensorEvent.values.clone();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
