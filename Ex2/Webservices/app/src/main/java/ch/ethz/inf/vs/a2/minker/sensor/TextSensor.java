package ch.ethz.inf.vs.a2.minker.sensor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.regex.Pattern;

import ch.ethz.inf.vs.a2.minker.http.HttpRawRequestImpl;

/**
 * Created by Chris on 17.10.2017.
 */

public class TextSensor extends AbstractSensor {
    private Socket MyClient;


    @Override
    public String executeRequest() throws Exception {
        String out = "";

        HttpURLConnection myUrlConnection;

        try {
            URL myURL = new URL("http://vslab.inf.ethz.ch:8081/sunspots/Spot1/sensors/temperature");
            myUrlConnection = (HttpURLConnection) myURL.openConnection();

            //myUrlConnection.setRequestProperty("Host","vslab.inf.ethz.ch:8081");
            myUrlConnection.setRequestProperty("Accept","text/plain");
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
        if(response != null && !response.equals("")){return Double.valueOf(response);}
        else { return Double.NaN; }
    }
}
