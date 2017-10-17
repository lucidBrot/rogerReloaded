package ch.ethz.inf.vs.a1.minker.sensors;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Iterator;

public class SensorActivity extends AppCompatActivity implements SensorEventListener{


    private int numberValues;
    private TextView sensorValuesTextView;
    private GraphView sensorValuesGraph;
    private TextView title;
    private int sensorType;

    private SensorTypesImpl sensorHandler;
    private SensorManager mSensorManager;
    private Sensor currSensor;
    private GraphContainerImpl graphContainer;

    private String unit;

    private String sensorName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        sensorHandler = new SensorTypesImpl();

        numberValues = 3;

        int sensorIndex = this.getIntent().getIntExtra("SensorIndex",0);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        currSensor = MainActivity.getSensor(sensorIndex);

        title = (TextView) findViewById(R.id.title);

        sensorValuesTextView = (TextView) findViewById(R.id.textView);

        sensorValuesGraph = (GraphView) findViewById(R.id.graph);

        if(currSensor != null){
            sensorType = currSensor.getType();
            numberValues = sensorHandler.getNumberValues(sensorType);
            sensorName = currSensor.getName();
            title.setText(sensorName);
            unit = sensorHandler.getUnitString(sensorType);
        }

        if(savedInstanceState != null){
            graphContainer = savedInstanceState.getParcelable("graph_container");

        } else {
            graphContainer = new GraphContainerImpl(numberValues);
        }

        if(currSensor != null) {
            for (LineGraphSeries<DataPoint> currSeries : graphContainer.getLineGraphArrayList()) {
                sensorValuesGraph.addSeries(currSeries);
            }

            if (unit != "no unit") {
                GridLabelRenderer gridLabel = sensorValuesGraph.getGridLabelRenderer();
                gridLabel.setVerticalAxisTitle(unit);
            }

            sensorValuesGraph.getViewport().setXAxisBoundsManual(true);
        }
        //sensorValuesGraph.getViewport().setYAxisBoundsManual(true);

        /* WORK IN PROGRESS
        if(currSensor != null){
            float maxRange = currSensor.getMaximumRange();
            sensorValuesGraph.getViewport().setMinY(-maxRange);
            sensorValuesGraph.getViewport().setMaxY(maxRange);
        }*/

        //sensorValuesGraph.addSeries(mSeries1);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putParcelable("graph_container",graphContainer);
        super.onSaveInstanceState(savedInstanceState);

    }

    public GraphContainer getGraphContainer() {
        return graphContainer;
    }

    private void updateGraph(double seconds,float[] values){

        //sensorValuesGraph.removeAllSeries();

        double minX = 0 ,maxX = 0;

        for(LineGraphSeries<DataPoint> currSeries:graphContainer.getLineGraphArrayList()){
            //sensorValuesGraph.addSeries(currSeries);
            maxX = Math.max(maxX,currSeries.getHighestValueX());
            minX = Math.max(minX,currSeries.getLowestValueX());
        }

        sensorValuesGraph.getViewport().setMinX(minX);
        sensorValuesGraph.getViewport().setMaxX(maxX);

        graphContainer.addValues(seconds,values);

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double seconds = System.currentTimeMillis()/1000.;

        float[] newSensorValues = new float[numberValues];
        for(int i = 0; i < numberValues; i++){
            newSensorValues[i] = sensorEvent.values[i];
        }

        updateGraph(seconds,newSensorValues);

        String textViewText = "";

        for (int i = 0; i < numberValues; i++){
            textViewText += String.valueOf(newSensorValues[i]) + "\t";
            if(unit != "no unit"){
                textViewText += unit;
            }
            textViewText += "\n";
        }

        sensorValuesTextView.setText(textViewText);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(currSensor != null && mSensorManager != null) {
            mSensorManager.registerListener(this, currSensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mSensorManager != null){
            mSensorManager.unregisterListener(this);
        }
    }
}
