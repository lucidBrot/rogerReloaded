package ch.ethz.inf.vs.a1.minker.sensors;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.provider.ContactsContract;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by christianknieling on 10.10.17.
 */

public class GraphContainerImpl implements Parcelable,GraphContainer {

    private static long startingTime;
    private static final int MAX_VALUES = 100;
    private int numberOfValues;
    private int numberOfLineGraphs;
    private transient ArrayList<LineGraphSeries<DataPoint>> lineGraphSeriesArrayList;
    private static int[] mColor = {Color.argb(255, 100, 255, 100),Color.argb(255, 100, 255, 255),Color.argb(255, 255, 100, 100),Color.argb(255, 255, 255, 100)};


    protected GraphContainerImpl(int i) {

        numberOfLineGraphs = i;
        startingTime = System.currentTimeMillis();
        lineGraphSeriesArrayList = new ArrayList<>(numberOfLineGraphs);
        for(int j = 0; j < numberOfLineGraphs; j++){
            LineGraphSeries<DataPoint> tmpSeries = new LineGraphSeries<>();
            if(j < 4 && j >= 0){
                tmpSeries.setColor(mColor[j]);
            }
            lineGraphSeriesArrayList.add(tmpSeries);
        }
    }

    protected GraphContainerImpl(Parcel in) {
        numberOfValues = in.readInt();
        numberOfLineGraphs = in.readInt();
        lineGraphSeriesArrayList = new ArrayList<>(numberOfLineGraphs);
        for(int j = 0; j < numberOfLineGraphs;j++){
            LineGraphSeries<DataPoint> currSeries = new LineGraphSeries<>();

            if(j < 4 && j >= 0){
                currSeries.setColor(mColor[j]);
            }

            for(int i = 0; i < numberOfValues; i++){
                currSeries.appendData((DataPoint) in.readSerializable(),false,MAX_VALUES);
            }

            lineGraphSeriesArrayList.add(currSeries);

        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(numberOfValues);
        dest.writeInt(numberOfLineGraphs);
        for (LineGraphSeries<DataPoint> currSeries : lineGraphSeriesArrayList){
            Iterator<DataPoint> iter2 = currSeries.getValues(Double.MIN_VALUE,Double.MAX_VALUE); //getting all values
            for(Iterator<DataPoint> iter = iter2;iter.hasNext();){
                dest.writeSerializable(iter.next());
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GraphContainerImpl> CREATOR = new Creator<GraphContainerImpl>() {
        @Override
        public GraphContainerImpl createFromParcel(Parcel in) {
            return new GraphContainerImpl(in);
        }

        @Override
        public GraphContainerImpl[] newArray(int size) {
            return new GraphContainerImpl[size];
        }
    };


    @Override
    public void addValues(double xIndex, float[] values) {
        int i = 0;
        if(values.length != numberOfLineGraphs){
            throw new IllegalArgumentException();
        }

        for(LineGraphSeries<DataPoint> currSeries:getLineGraphArrayList()){
            double newTime = xIndex - (startingTime/1000.);
            currSeries.appendData(new DataPoint(newTime,(double) values[i]), false, MAX_VALUES);
            i++;
        }

        numberOfValues++;
        numberOfValues = Math.min(numberOfValues,MAX_VALUES);
    }

    @Override
    public float[][] getValues() {
        float[][] values = new float[numberOfValues][numberOfLineGraphs];

        int i = 0;
        int j;

        for(LineGraphSeries<DataPoint> tmpLineGraph : lineGraphSeriesArrayList) {
            Iterator<DataPoint> iter2 = tmpLineGraph.getValues(tmpLineGraph.getLowestValueX(), tmpLineGraph.getHighestValueX());
            j = 0;
            for(Iterator<DataPoint> iter = iter2;iter.hasNext();){
                DataPoint nextDatapoint = iter.next();
                values[j][i] = (float) nextDatapoint.getY();
                j++;
            }
            i++;
        }
        return values;
    }

    public ArrayList<LineGraphSeries<DataPoint>> getLineGraphArrayList(){
        return lineGraphSeriesArrayList;
    }
}
