package ch.ethz.inf.vs.a2.minker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ch.ethz.inf.vs.a2.sensor.AbstractSensor;

/**
 * Created by Chris on 17.10.2017.
 */

public class JsonSensor extends AbstractSensor {

    @Override
    public String executeRequest() throws Exception {
        String out = "";

        HttpURLConnection myUrlConnection;

        try {
            URL myURL = new URL("http://vslab.inf.ethz.ch:8081/sunspots/Spot1/sensors/temperature");
            myUrlConnection = (HttpURLConnection) myURL.openConnection();

            //myUrlConnection.setRequestProperty("Host","vslab.inf.ethz.ch:8081");
            myUrlConnection.setRequestProperty("Accept","application/json");
            myUrlConnection.setRequestProperty("Connection","close");

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(myUrlConnection.getInputStream()));

            String line;

            // read from the urlconnection via the bufferedreader
            while ((line = bufferedReader.readLine()) != null)
            {
                out += (line);
            }
            bufferedReader.close();

            return out;
        }
        catch (IOException e) {
            System.out.println(e);
        }
        return out;
    }

    @Override
    public double parseResponse(String response) {
        try {
            JSONObject responseJSON = new JSONObject(response);
            double returnInt = responseJSON.getDouble("value");
            return returnInt;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
