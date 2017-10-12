package ch.ethz.inf.vs.a1.minker.ble;

import android.app.Fragment;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Stack;
import java.util.UUID;

import static ch.ethz.inf.vs.a1.minker.ble.SensirionSHT31UUIDS.NOTIFICATION_DESCRIPTOR_UUID;
import static ch.ethz.inf.vs.a1.minker.ble.SensirionSHT31UUIDS.UUID_HUMIDITY_CHARACTERISTIC;
import static ch.ethz.inf.vs.a1.minker.ble.SensirionSHT31UUIDS.UUID_HUMIDITY_SERVICE;
import static ch.ethz.inf.vs.a1.minker.ble.SensirionSHT31UUIDS.UUID_TEMPERATURE_CHARACTERISTIC;
import static ch.ethz.inf.vs.a1.minker.ble.SensirionSHT31UUIDS.UUID_TEMPERATURE_SERVICE;

/**
 * Created by Josua on 11.10.2017.
 */

public class GraphFragment extends Fragment {
    private GraphView graph;

    LineGraphSeries<DataPoint> humiditySeries = new LineGraphSeries<>();
    LineGraphSeries<DataPoint> temperatureSeries = new LineGraphSeries<>();

    private static final int MAX_DATAPOINTS = 100;

    private BluetoothGatt mGatt;

    private BluetoothDevice currentDevice;
    private Stack<BluetoothGattDescriptor> descriptorStack;

    private LinearLayout connection_state;
    private long startingTime;

    private boolean connected = false;

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.i("onConnectionStateChange", "Status: " + status);
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.i("gattCallback", "STATE_CONNECTED");
                    mGatt = gatt;
                    mGatt.discoverServices();
                    connected = true;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (connection_state !=null)
                                connection_state.setVisibility(View.INVISIBLE);
                            Log.i("Fragment","Deactivationg Connecting layout");
                        }
                    });

                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.e("gattCallback", "STATE_DISCONNECTED");
                    mGatt = gatt;
                    getActivity().finish();
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

            BluetoothGattDescriptor humidityDescriptor = humidityCharacteristic.getDescriptor(NOTIFICATION_DESCRIPTOR_UUID);
            humidityDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            BluetoothGattDescriptor temperatureDescriptor = temperatureCharacteristic.getDescriptor(NOTIFICATION_DESCRIPTOR_UUID);
            temperatureDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);

            descriptorStack = new Stack<>();
            descriptorStack.push(temperatureDescriptor);
            gatt.writeDescriptor(humidityDescriptor);
            startingTime = System.currentTimeMillis();
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            Log.i("hooooi", "i'm temmie");
            if (!descriptorStack.isEmpty()) {
                gatt.writeDescriptor(descriptorStack.pop());
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);



            byte[] values = characteristic.getValue();
            float convertedValues = convertRawValue(values);

            UUID uuid = characteristic.getUuid();

            double current_time = (System.currentTimeMillis() - startingTime) / 1000.;
            final DataPoint dataPoint = new DataPoint(current_time,convertedValues);
            if(uuid.equals(UUID_HUMIDITY_CHARACTERISTIC)){
                //Log.i("Characteristic", "Got Humidity Values: " + convertedValues);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (humiditySeries !=null)
                            humiditySeries.appendData(dataPoint, true, MAX_DATAPOINTS);
                    }
                });
            }
            else if(uuid.equals(UUID_TEMPERATURE_CHARACTERISTIC)){
                //Log.i("Characteristic", "Got Temperature Values: " + convertedValues);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (temperatureSeries !=null)
                            temperatureSeries.appendData(dataPoint, true, MAX_DATAPOINTS);
                    }
                });
            }

        }
    };

    private static float convertRawValue(byte[] raw){
        ByteBuffer wrapper = ByteBuffer.wrap(raw).order(ByteOrder.LITTLE_ENDIAN);
        return wrapper.getFloat();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        humiditySeries.setColor(Color.BLUE);
        temperatureSeries.setColor(Color.RED);
        Log.i("Fragment", "Hello I'm fragment");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_graph, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getView() != null ) {
            graph = getView().findViewById(R.id.data_graph);
            graph.getViewport().setXAxisBoundsManual(true);
            graph.addSeries(humiditySeries);
            graph.addSeries(temperatureSeries);
            graph.getViewport().setMinX(0);
            graph.getViewport().setMaxX(20);
            connection_state = (LinearLayout) getView().findViewById(R.id.connect_state);
            if(connected)
                connection_state.setVisibility(View.INVISIBLE);
            Bundle args = getArguments();
            if(currentDevice == null && args != null) {
                currentDevice = args.getParcelable("device");
                currentDevice.connectGatt(getActivity(), false, gattCallback);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //mGatt.disconnect();
        if(mGatt != null)
            mGatt.close();
    }
}

