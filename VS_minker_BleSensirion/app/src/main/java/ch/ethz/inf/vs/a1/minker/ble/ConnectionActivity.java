package ch.ethz.inf.vs.a1.minker.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.UUID;

import static ch.ethz.inf.vs.a1.minker.ble.SensirionSHT31UUIDS.NOTIFICATION_DESCRIPTOR_UUID;
import static ch.ethz.inf.vs.a1.minker.ble.SensirionSHT31UUIDS.UUID_HUMIDITY_CHARACTERISTIC;
import static ch.ethz.inf.vs.a1.minker.ble.SensirionSHT31UUIDS.UUID_HUMIDITY_SERVICE;
import static ch.ethz.inf.vs.a1.minker.ble.SensirionSHT31UUIDS.UUID_TEMPERATURE_CHARACTERISTIC;
import static ch.ethz.inf.vs.a1.minker.ble.SensirionSHT31UUIDS.UUID_TEMPERATURE_SERVICE;

public class ConnectionActivity extends AppCompatActivity {

    private static final int MAX_DATAPOINTS = 100;

    private BluetoothGatt mGatt;
    //private BluetoothAdapter mBluetoothAdapter;

    private BluetoothDevice currentDevice;

    private GraphView graph;
    private LinearLayout connection_state;

    private long startingTime;

    LineGraphSeries<DataPoint> humiditySeries = new LineGraphSeries<>();
    LineGraphSeries<DataPoint> temperatureSeries = new LineGraphSeries<>();

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.i("onConnectionStateChange", "Status: " + status);
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.i("gattCallback", "STATE_CONNECTED");
                    mGatt = gatt;
                    mGatt.discoverServices();
                    connection_state.setVisibility(View.INVISIBLE);

                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.e("gattCallback", "STATE_DISCONNECTED");
                    mGatt = gatt;
                    break;
                default:
                    Log.e("gattCallback", "STATE_OTHER");
                    mGatt = gatt;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);

            BluetoothGattService humidityService = gatt.getService(UUID_HUMIDITY_SERVICE);
            BluetoothGattService temperatureService = gatt.getService(UUID_TEMPERATURE_SERVICE);

            BluetoothGattCharacteristic humidityCharacteristic = humidityService.getCharacteristic(UUID_HUMIDITY_CHARACTERISTIC);
            BluetoothGattCharacteristic temperatureCharacteristic = temperatureService.getCharacteristic(UUID_TEMPERATURE_CHARACTERISTIC);

            gatt.setCharacteristicNotification(humidityCharacteristic, true);
            gatt.setCharacteristicNotification(temperatureCharacteristic, true);

            BluetoothGattDescriptor humidityDescriptor = new BluetoothGattDescriptor(NOTIFICATION_DESCRIPTOR_UUID, BluetoothGattDescriptor.PERMISSION_WRITE);
            humidityDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            BluetoothGattDescriptor temperatureDescriptor = new BluetoothGattDescriptor(NOTIFICATION_DESCRIPTOR_UUID, BluetoothGattDescriptor.PERMISSION_WRITE);
            temperatureDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);

            humidityCharacteristic.addDescriptor(humidityDescriptor);
            temperatureCharacteristic.addDescriptor(temperatureDescriptor);

            gatt.beginReliableWrite();
            gatt.writeDescriptor(humidityDescriptor);
            gatt.writeDescriptor(temperatureDescriptor);
            startingTime = System.currentTimeMillis();
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            UUID uuid = characteristic.getUuid();

            byte[] values = characteristic.getValue();
            float convertedValues = convertRawValue(values);
            Log.i("Characteristic", "Got Values: " + convertedValues);
            double current_time = (System.currentTimeMillis() - startingTime) / 1000.;
            DataPoint dataPoint = new DataPoint(current_time,convertedValues);
            humiditySeries.appendData(dataPoint, true, MAX_DATAPOINTS);

            graph.getViewport().setMinX(humiditySeries.getLowestValueX());
            graph.getViewport().setMaxX(current_time);
        }
    };

    private static float convertRawValue(byte[] raw){
        ByteBuffer wrapper = ByteBuffer.wrap(raw).order(ByteOrder.LITTLE_ENDIAN);
        return wrapper.getFloat();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        currentDevice = getIntent().getParcelableExtra("device");
        currentDevice.connectGatt(this, false, gattCallback);

        connection_state = (LinearLayout)findViewById(R.id.connect_state);
        graph = (GraphView) findViewById(R.id.data_graph);
        graph.getViewport().setXAxisBoundsManual(true);

        humiditySeries.setColor(Color.BLUE);
        temperatureSeries.setColor(Color.RED);
        graph.addSeries(humiditySeries);
        graph.addSeries(temperatureSeries);


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onBackPressed() {
        if(mGatt != null) {
            //currentDevice.
            mGatt.disconnect();
            mGatt.close();
        }
        super.onBackPressed();
    }
}
