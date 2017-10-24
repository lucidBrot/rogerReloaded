package ch.ethz.inf.vs.a2.minker.sensor;

import android.hardware.Sensor;

/**
 * Created by Chris on 09.10.2017.
 */

public class SensorTypes {
    public static int getNumberValues(int sensorType) {
        int retVal;

        switch (sensorType){
            case Sensor.TYPE_ACCELEROMETER:             retVal = 3;
                break;

            case Sensor.TYPE_AMBIENT_TEMPERATURE:       retVal = 1;
                break;

            case Sensor.TYPE_GYROSCOPE:                 retVal = 3;
                break;

            case Sensor.TYPE_GRAVITY:                   retVal = 3;
                break;

            case Sensor.TYPE_LIGHT:                     retVal = 1;
                break;

            case Sensor.TYPE_LINEAR_ACCELERATION:       retVal = 3;
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:            retVal = 3;
                break;

            case Sensor.TYPE_PRESSURE:                  retVal = 1;
                break;

            case Sensor.TYPE_RELATIVE_HUMIDITY:         retVal = 1;
                break;

            case Sensor.TYPE_ROTATION_VECTOR:           retVal = 5;
                break;

            case Sensor.TYPE_PROXIMITY:                 retVal = 1;
                break;

            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED: retVal = 6;
                break;

            case Sensor.TYPE_GAME_ROTATION_VECTOR:      retVal = 5;
                break;

            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:    retVal = 6;
                break;

            case Sensor.TYPE_POSE_6DOF:                 retVal = 15;
                break;

            case Sensor.TYPE_STATIONARY_DETECT:         retVal = 1;
                break;

            case Sensor.TYPE_HEART_BEAT:                retVal = 1;
                break;

            case Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT:retVal = 1;
                break;

            case Sensor.TYPE_ACCELEROMETER_UNCALIBRATED:retVal = 6;
                break;


            default:                                retVal = 1;
        }

        return retVal;
    }

    public static String getUnitString(int sensorType) {
        String retVal;

        switch (sensorType){
            case Sensor.TYPE_ACCELEROMETER:             retVal = "m/s^2";
                break;

            case Sensor.TYPE_AMBIENT_TEMPERATURE:       retVal = "°C";
                break;

            case Sensor.TYPE_GYROSCOPE:                 retVal = "rad/s";
                break;

            case Sensor.TYPE_GRAVITY:                   retVal = "m/s^2";
                break;

            case Sensor.TYPE_LIGHT:                     retVal = "lx";
                break;

            case Sensor.TYPE_LINEAR_ACCELERATION:       retVal = "m/s^2";
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:            retVal = "microT";
                break;

            case Sensor.TYPE_PRESSURE:                  retVal = "hPa";
                break;

            case Sensor.TYPE_RELATIVE_HUMIDITY:         retVal = "%";
                break;

            case Sensor.TYPE_ROTATION_VECTOR:           retVal = "no unit";
                break;

            case Sensor.TYPE_PROXIMITY:                 retVal = "cm";
                break;

            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED: retVal = "microT";
                break;

            case Sensor.TYPE_GAME_ROTATION_VECTOR:      retVal = "no unit";
                break;

            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:    retVal = "rad/s";
                break;

            case Sensor.TYPE_POSE_6DOF:                 retVal = "no unit";
                break;

            case Sensor.TYPE_STATIONARY_DETECT:         retVal = "no unit";
                break;

            case Sensor.TYPE_HEART_BEAT:                retVal = "no unit";
                break;

            case Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT:retVal = "no unit";
                break;

            case Sensor.TYPE_ACCELEROMETER_UNCALIBRATED:retVal = "m/s^2";
                break;

            default:                                retVal = "no unit";
        }

        return retVal;
    }
}
