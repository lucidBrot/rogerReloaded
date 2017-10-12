package ch.ethz.inf.vs.a1.minker.ble;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private final int FINE_LOCATION_REQUEST = 2;
    private final int REQUEST_ENABLE_BT = 1;
    private final int REQUEST_ENABLE_LOC = 2;
    private final String DEVICE_NAME_FILTER = "Smart Humigadget";


    private Handler bleHandler;
    private Runnable timeoutCallback;
    private static final long SCAN_PERIOD = 10000;


    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mLEScanner;
    private List<ScanFilter> filters;
    private ScanSettings settings;
    private ScanCallback resultCallback;
    private ProgressBar scanBar;
    private AlertDialog.Builder locationAlert;
    private LocationManager locationManager;

    private ArrayAdapter<String> devicesAdapter;
    private ArrayList<BluetoothDevice> foundDevices = new ArrayList<BluetoothDevice>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE is not supported on this device", Toast.LENGTH_LONG).show();
            finish();
        }
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            locationAlert = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert);
        } else {
            locationAlert = new AlertDialog.Builder(this);
        }



        locationAlert.setTitle("Location Service Enable")
                .setMessage("In order to scan for Bluetooth Low Energy Devices you have to activate Location Service")
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent enableLocation = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableLocation, REQUEST_ENABLE_LOC);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);



        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_REQUEST);
        }

        final ScanFilter filter = new ScanFilter.Builder().setDeviceName(DEVICE_NAME_FILTER).build();
        filters = new ArrayList<ScanFilter>();
        filters.add(filter);
        settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_POWER).setReportDelay(0).build();

        devicesAdapter = new ArrayAdapter<String>(this, R.layout.activity_list);
        ListView lv = (ListView)findViewById(R.id.device_list);
        lv.setAdapter(devicesAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!requestPermissions(true, false)){
                    mLEScanner.stopScan(resultCallback);
                    Intent nextActivity = new Intent(parent.getContext(), ConnectionActivity.class).putExtra("device", foundDevices.get(position));
                    startActivity(nextActivity);
                }
            }
        });

        mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();

        resultCallback = new ScanCallback() {
            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.i("Scab","Scan failed");
            }

            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                Log.i("[APP]", "******************************************");
                Log.i("[APP]", "The scan result " + result);
                Log.i("[APP]", "------------------------------------------");
                if(!foundDevices.contains(result.getDevice())){
                    foundDevices.add(result.getDevice());
                    if (result.getDevice().getName() != null)
                        devicesAdapter.add(result.getDevice().getName() + " (" + result.getDevice().getAddress() + ")");
                    else
                        devicesAdapter.add(result.getDevice().toString());
                    devicesAdapter.notifyDataSetChanged();
                }
            }
        };

        scanBar = (ProgressBar) findViewById(R.id.scanbar);
        scanBar.setVisibility(View.INVISIBLE);

        timeoutCallback = new Runnable() {
            @Override
            public void run() {
                if(!(mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled())) {
                    mLEScanner.stopScan(resultCallback);
                }
                scanBar.setIndeterminate(false);
                scanBar.setVisibility(View.INVISIBLE);
            }
        };
        bleHandler = new Handler();


        Button button = (Button) findViewById(R.id.scan_button);
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //Toast.makeText(this, "Button Clicked", Toast.LENGTH_LONG).show();
                if(!requestPermissions()){
                    if (mLEScanner == null){
                        mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
                    }
                    mLEScanner.stopScan(resultCallback);
                    scanBar.setIndeterminate(false);
                    scanBar.setVisibility(View.INVISIBLE);
                    devicesAdapter.clear();
                    foundDevices.clear();
                    mLEScanner.startScan(filters, settings, resultCallback);
                    bleHandler.removeCallbacks(timeoutCallback);
                    bleHandler.postDelayed(timeoutCallback, SCAN_PERIOD);
                    scanBar.setIndeterminate(true);
                    scanBar.setVisibility(View.VISIBLE);
                }
            }
        });
        requestPermissions();
    }

    private boolean requestPermissions(){
        return requestPermissions(true, true);
    }

    private boolean requestPermissions(boolean checkBluetooth, boolean checkLocation){
        boolean neededPermission = false;
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && checkLocation){
            locationAlert.show();
            neededPermission = true;
        }

        if((mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) && checkBluetooth){
            Intent enableBtintent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtintent, REQUEST_ENABLE_BT);
            neededPermission = true;
        }
        return  neededPermission;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case FINE_LOCATION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {



                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!(mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) && mLEScanner != null)
            mLEScanner.stopScan(resultCallback);
    }
}
