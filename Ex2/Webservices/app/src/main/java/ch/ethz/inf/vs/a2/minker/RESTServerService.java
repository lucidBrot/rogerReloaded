package ch.ethz.inf.vs.a2.minker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ch.ethz.inf.vs.a2.sensor.SensorTypes;

/**
 * Created by Josua on 10/20/17.
 */

public class RESTServerService extends Service {

    private final static String TAG = "RESTService";
    private static final int NOTIFICATION_ID = 500;

    private ServerSocket serverSocket;
    private int port;

    private ArrayList<android.hardware.Sensor> sensorList;

    private final IBinder binder = new LocalBinder();

    private RESTServerActivity activity;
    public ArrayAdapter<String> logAdapter;


    private RESTUrlMapper mapper;
    private NotificationManager notificationManager;

    class LocalBinder extends Binder {
        RESTServerService getService() {
            return RESTServerService.this;
        }
    }

    private Runnable serviceThread = new Runnable() {
        @Override
        public void run() {
            Log.i(TAG, "Service Started");
            while (!serverSocket.isClosed()) {
                try {
                    final Socket accept = serverSocket.accept();
                    new RESTRequestTask(RESTServerService.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, accept);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            Log.i(TAG, "Service shut down");
        }
    };

    public void setActivity(RESTServerActivity activity) {
        this.activity = activity;
    }

    public void logToActivity(final String message) {
        if (activity != null) {
            Locale locale;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                locale = activity.getResources().getConfiguration().getLocales().get(0);
            } else {
                //noinspection deprecation
                locale = activity.getResources().getConfiguration().locale;
            }
            String time = String.format(locale, "[%1$tY-%1$tm-%1$td] %1$tH:%1$tm", Calendar.getInstance().getTime());
            final String logMessage = time + " " + message;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    logAdapter.add(logMessage);
                    logAdapter.notifyDataSetChanged();
                }
            });
            activity.scrollToBottom();
            Log.i(TAG, logMessage);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        logAdapter = new ArrayAdapter<String>(this, R.layout.log_entry);
        mapper = new RESTUrlMapper();
        sensorList = new ArrayList<>();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        registerEndpoints();
    }

    private void registerEndpoints() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                SensorManager sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
                List<android.hardware.Sensor> sensors = sensorMgr.getSensorList(android.hardware.Sensor.TYPE_ALL);
                String sensorsIndex = "";
                for (final android.hardware.Sensor sensor : sensors) {
                    sensorList.add(sensor);
                    sensorsIndex += "<li><a href=\"/sensors/" + sensor.getName().replace(" ", "_") + "/\">" + sensor.getName().replace(" ", "_") + "</a></li>";
                    final int countVals = SensorTypes.getNumberValues(sensor.getType());
                    final String sensorUnitName = SensorTypes.getUnitString(sensor.getType());
                    RESTSensorHandler handler = new RESTSensorHandler(sensor, countVals, sensorUnitName);
                    sensorMgr.registerListener(handler, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                    mapper.registerHandler("/sensors/" + sensor.getName().replace(" ", "_") + "/", handler);
                }
                final String allSensors = sensorList.toString();
                final String sensorList = sensorsIndex;
                mapper.registerHandler("/sensors/", new IRESTMappedHandler() {
                    @Override
                    public String handle(Map<String, String> request, Map<String, String> header, Map<String, String> param) {
                        String head = "<html><head><title>Index Sensors</title></head>";
                        String body = "<body><h1>Index Sensors</h1><p><a href=\"/\">Back</a></p><ul>" + sensorList + "</ul></body></html>";
                        return head + body;
                    }
                });
                mapper.registerHandler("/actuators/", new IRESTMappedHandler() {
                    @Override
                    public String handle(Map<String, String> request, Map<String, String> header, Map<String, String> param) {
                        String head = "<html><head><title>Index Actuators</title></head>";
                        String body = "<body><h1>Index Actuators</h1><p><a href=\"/\">Back</a></p><ul>" +
                                "<li><a href=\"/actuators/vibrate/\">Vibration</a></li>" +
                                "</ul></body></html>";
                        return head + body;
                    }
                });
                mapper.registerHandler("/actuators/vibrate/", new IRESTMappedHandler() {
                    @Override
                    public String handle(Map<String, String> request, Map<String, String> header, Map<String, String> param) {
                        String head = "<html><head><title>Vibration</title></head>";
                        String body = "<body><h1>Vibration</h1><p><a href=\"/actuators/\">Back</a></p>" +
                                "<p>Send POST request to /actuators/vibrate/ to activate vibration</p>" +
                                "<form method=\"POST\" action=\"/actuators/vibrate/\"><button type=\"submit\">Vibrate</button></form></body></html>";
                        if (request.get("method").toLowerCase().equals("post")) {
                            Vibrator vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                            //Deprecated in API Level 26 current min is 21
                            vib.vibrate(200);
                        }
                        return head + body;
                    }
                });
//                mapper.registerHandler("/actuators/flashlight/", new IRESTMappedHandler() {
//                    private boolean activated = true;
//
//                    @Override
//                    public String handle(Map<String, String> request, Map<String, String> header, Map<String, String> param) {
//                        String head = "<html><head><title>Flashlight</title></head>";
//                        String body = "<body><h1>Flashlight</h1><p><a href=\"/actuators/\">Back</a></p>" +
//                                "<p>Send POST request to /actuators/flashlight/ to toggle flashlight</p>" +
//                                "<form method=\"POST\" action=\"/actuators/flashlight/\"><button type=\"submit\">Toggle</button></form></body></html>";
//                        if (request.get("method").toLowerCase().equals("post")) {
//                            if (!activated) {
//                                CameraManager cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
//                                try {
//                                    cameraManager.setTorchMode(cameraManager.getCameraIdList()[0], true);
//                                } catch (CameraAccessException e) {
//                                    e.printStackTrace();
//                                }
//
//                            } else {
//                            }
//                        }
//                        return head + body;
//                    }
//                });
                return null;
            }
        }.

                execute();
        mapper.registerHandler("/", new

                IRESTMappedHandler() {
                    @Override
                    public String handle
                            (Map<String, String> request, Map<String, String> header, Map<String, String> param) {

                        String head = "<html><head><title>Index</title></head>";
                        String body = "<body><h1>Index</h1><p><a href=\"/sensors/\">sensors</a></p>" +
                                "<p><a href=\"/actuators\">actuators/</a></p></body></html>";
                        return head + body;
                    }
                });
    }

    public RESTUrlMapper getUrlMapper() {
        return mapper;
    }

    public void startService(int port) {
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(port));
            new Thread(serviceThread).start();

            PendingIntent intent = PendingIntent.getActivity(this, 0, new Intent(this, RESTServerActivity.class), 0);
            Notification notification = new Notification.Builder(this).setContentTitle("Webserver running")
                    .setContentText("Webserver running")
                    .setAutoCancel(false)
                    .setTicker("Webserver running")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(intent)
                    .setOngoing(true)
                    .build();
            notificationManager.notify(NOTIFICATION_ID, notification);
        } catch (IOException e) {
            Log.e(TAG, "Could not open socket");
            e.printStackTrace();
        }

    }

    public void stopService() {
        super.onDestroy();
        try {
            serverSocket.close();
            notificationManager.cancelAll();
        } catch (Exception e) {
            Log.e(TAG, "Failed to close ServerSocket");
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    public boolean isRunning() {
        return serverSocket != null && !serverSocket.isClosed() && serverSocket.isBound();
    }
}
