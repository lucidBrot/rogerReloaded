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

public class SensorActivity extends AppCompatActivity implements SensorEventListener,GraphContainer{

    private long startingTime;
    private int maxValues;

    private TextView sensorValuesTextView;
    private GraphView sensorValuesGraph;
    private TextView title;

    private SensorTypesImpl sensorHandler;
    private SensorManager mSensorManager;
    private Sensor currSensor;

    private int numberValues;
    private String unit;

    private float[][] sensorValues;
    private double[] xValues;
    private int oldestIndex;
    private int myIndex;
    private boolean arrayFull;
    private ArrayList<LineGraphSeries<DataPoint>> lineGraphSeriesArrayList;
    private int[] mColor = new int[4];

    private String sensorName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        sensorHandler = new SensorTypesImpl();
        maxValues = 100;

        mColor[0] = Color.argb(255, 100, 255, 100); // g
        mColor[1] = Color.argb(255, 255, 255, 100); // y
        mColor[2] = Color.argb(255, 255, 100, 100); // r
        mColor[3] = Color.argb(255, 100, 255, 255); // c

        int sensorIndex = this.getIntent().getIntExtra("SensorIndex",0);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        currSensor = MainActivity.getSensor(sensorIndex);

        title = (TextView) findViewById(R.id.title);

        if(savedInstanceState != null){
            xValues = savedInstanceState.getDoubleArray("x_values");
            numberValues = savedInstanceState.getInt("number_values");

            float[] sensorValuesTmp = savedInstanceState.getFloatArray("matrix_values");
            int sizeX = savedInstanceState.getInt("matrix_sizeX");
            int sizeY = savedInstanceState.getInt("matrix_sizeY");
            maxValues = savedInstanceState.getInt("max_values");

            sensorValues = new float[maxValues][sizeY];

            int i = 0, j = 0;
            for(float item:sensorValuesTmp){
                sensorValues[i][j] = item;
                i++;
                if(i >= sizeX){
                    j++;
                    i = 0;
                }
            }

            oldestIndex = savedInstanceState.getInt("oldest_index");
            myIndex = savedInstanceState.getInt("my_index");
            startingTime = savedInstanceState.getLong("starting_time");
        } else {
            myIndex = 0;
            oldestIndex = -1;
            numberValues = 3;
            arrayFull = false;

            if(currSensor != null){
                int sensorType = currSensor.getType();
                numberValues = sensorHandler.getNumberValues(sensorType);
            }

            xValues = new double[maxValues];
            sensorValues = new float[maxValues][numberValues];
            startingTime = System.currentTimeMillis();
        }



        if(currSensor != null){
            int sensorType = currSensor.getType();
            sensorName = currSensor.getName();
            title.setText(sensorName);
            unit = sensorHandler.getUnitString(sensorType);
        }

        lineGraphSeriesArrayList = new ArrayList<>(3);

        sensorValuesTextView = (TextView) findViewById(R.id.textView);

        sensorValuesGraph = (GraphView) findViewById(R.id.graph);

        if(unit != "no unit"){
            GridLabelRenderer gridLabel = sensorValuesGraph.getGridLabelRenderer();
            gridLabel.setVerticalAxisTitle(unit);
        }

        sensorValuesGraph.getViewport().setXAxisBoundsManual(true);
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
        savedInstanceState.putDoubleArray("x_values",xValues);

        savedInstanceState.putInt("number_values",numberValues);

        int sizeX = sensorValues.length;
        int sizeY = numberValues;
        int i = 0;
        float[] values = new float[sizeY * sizeX];

        for (float[] row : sensorValues) {
            for(float item:row){
                values[i] = item;
                i++;
            }
        }
        savedInstanceState.putInt("oldest_index",oldestIndex);
        savedInstanceState.putInt("my_index",myIndex);
        savedInstanceState.putFloatArray("matrix_values", values);
        savedInstanceState.putInt("matrix_sizeX", sizeX);
        savedInstanceState.putInt("matrix_sizeY", sizeY);
        savedInstanceState.putInt("max_values",maxValues);
        savedInstanceState.putLong("starting_time",startingTime);

        super.onSaveInstanceState(savedInstanceState);

    }

    public GraphContainer getGraphContainer() {
        return this;
    }

    private LineGraphSeries<DataPoint> createSeries(int j){
        float values[][] = getValues();
        double xValues[] = getXValues();

        int valueLength = myIndex;

        //System.out.println(valueLength);
        LineGraphSeries<DataPoint> output = new LineGraphSeries<>();

        double lastXval = 0;

        for(int i = 0; i < valueLength; i++){

            //System.out.println("i,j: " + i +","+ j + " values.length: " + values.length + " valuelength/myIndex: " + myIndex + " Numbervalues: " + numberValues);
            double xVal = xValues[i];
            double yVal = (double) values[i][j];
            //System.out.println("xVal,yVal: "+ xVal +", "+ yVal);
            if(lastXval < xVal){
                output.appendData(new DataPoint(xVal,yVal),true, maxValues);
            }
            lastXval = xVal;

        }
        if(j < 4){
            output.setColor(mColor[j]);
        }
        return output;
    }

    private void updateGraph(){
        sensorValuesGraph.removeAllSeries();
        double minX = 0 ,maxX = 0;//, minY = -Double.MAX_VALUE, maxY = -Double.MIN_VALUE;

        for(LineGraphSeries<DataPoint> currSeries:lineGraphSeriesArrayList){
            sensorValuesGraph.addSeries(currSeries);
            maxX = Math.max(maxX,currSeries.getHighestValueX());
            minX = Math.max(minX,currSeries.getLowestValueX());
            /*maxY = Math.max(maxY,currSeries.getHighestValueY());
            minY = Math.min(minY,currSeries.getLowestValueY());*/
        }


        /*sensorValuesGraph.getViewport().setXAxisBoundsManual(true);*/
        sensorValuesGraph.getViewport().setMinX(minX);
        sensorValuesGraph.getViewport().setMaxX(maxX);
        /*sensorValuesGraph.getViewport().setMinY(minY);
        sensorValuesGraph.getViewport().setMaxY(maxY);*/
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        float[] newSensorValues = new float[numberValues];
        for(int i = 0; i < numberValues; i++){
            newSensorValues[i] = sensorEvent.values[i];
        }
        double seconds = (System.currentTimeMillis() - startingTime)/1000.;

        addValues(seconds,newSensorValues);

        lineGraphSeriesArrayList.clear();

        for (int j = 0;j < numberValues;j++){
            lineGraphSeriesArrayList.add(createSeries(j));
            //sensorValuesGraph.addSeries(createSeries(j));
        }
        updateGraph();

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
            mSensorManager.registerListener(this, currSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mSensorManager != null){
            mSensorManager.unregisterListener(this);
        }
    }

    @Override
    public void addValues(double xIndex, float[] values) {
        if(values.length == numberValues){
            float[][] newValues = new float[Math.min(maxValues,(myIndex+1))][3];
            if(myIndex < maxValues && oldestIndex < myIndex){
                //Array isn't full yet -> just add value
                int i = 0;
                for (float item: values){
                    sensorValues[myIndex][i] = item;
                    i++;
                }

                xValues[myIndex] = xIndex;
            } else {
                //Array is full.. move everything 1 to the left and add new Value at the End
                float[][] newSensorValueArray = new float[maxValues][numberValues];
                double[] newxValueArray = new double[maxValues];

                System.arraycopy(sensorValues,1,newSensorValueArray,0,maxValues-1);

                int i = 0;
                for (float item: values){
                    newSensorValueArray[maxValues-1][i] = item;
                    i++;
                }

                System.arraycopy(xValues,1,newxValueArray,0,maxValues-1);
                newxValueArray[myIndex] = xIndex;

                sensorValues = newSensorValueArray;
                xValues = newxValueArray;
            }
            oldestIndex = myIndex;

            if(myIndex < maxValues-1){

                myIndex++;

            } else {
                arrayFull = true;
                myIndex = maxValues-1;
            }
        } else {
            throw new IllegalArgumentException();
        }


        /*for (int i = 0; i < values.length; i++){
            sensorValues[myIndex] = values;
            xValues[myIndex] = xIndex;
        }

        myIndex++;

        while (myIndex >= 100){
            myIndex -= 100;
            arrayFull = true;
        }*/

    }

    @Override
    public float[][] getValues() {
        /*

        if(oldestIndex < myIndex){
            returnValues = new float[myIndex-oldestIndex][3];

            System.arraycopy(sensorValues, oldestIndex, returnValues, 0, myIndex-oldestIndex);
        } else if (oldestIndex == myIndex && !arrayFull){
            returnValues = new float[0][3];
        } else {
            returnValues = new float[100][3];

            System.arraycopy(sensorValues, myIndex, returnValues, 0, 100-myIndex);

            System.arraycopy(sensorValues, 0, returnValues, 100-myIndex, myIndex);

        }
        */
        float[][] returnValues = new float[oldestIndex + 1][3];

        System.arraycopy(sensorValues,0,returnValues,0,oldestIndex + 1);
        return returnValues;
    }

    public double[] getXValues() {
        /*
        if(oldestIndex < myIndex){
            returnValues = new double[myIndex-oldestIndex];

            System.arraycopy(xValues, oldestIndex, returnValues, 0, myIndex-oldestIndex);

        } else if (oldestIndex == myIndex && !arrayFull){

            returnValues = new double[0];

        } else {
            returnValues = new double[100];

            System.arraycopy(xValues, myIndex, returnValues, 0, 100-myIndex);

            System.arraycopy(xValues, 0, returnValues, 100-myIndex, myIndex);

        }
        */

        double[] returnValues = new double[oldestIndex + 1];

        System.arraycopy(xValues,0,returnValues,0,oldestIndex + 1);
        return returnValues;
    }
}
