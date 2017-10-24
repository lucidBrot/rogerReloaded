package ch.ethz.inf.vs.a2.sensor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.regex.Pattern;

import ch.ethz.inf.vs.a2.http.HttpRawRequestImpl;

/**
 * Created by Chris on 17.10.2017.
 */

public class RawHttpSensor extends AbstractSensor {
    private Socket MyClient;


    @Override
    public String executeRequest() throws Exception {
        HttpRawRequestImpl RequestHandler = new HttpRawRequestImpl();
        String out = "";

        try {
            InetAddress address = InetAddress.getByName(new URL("http://vslab.inf.ethz.ch").getHost());
            String ip = address.getHostAddress();
            MyClient = new Socket(ip, 8081);
            PrintWriter pw = new PrintWriter(MyClient.getOutputStream());
            String getMessage = RequestHandler.generateRequest("vslab.inf.ethz.ch",8081,"/sunspots/Spot1/sensors/temperature");
            pw.print(getMessage);
            pw.flush();
            BufferedReader br = new BufferedReader(new InputStreamReader(MyClient.getInputStream()));
            String t;
            while((t = br.readLine()) != null) {out += t + "\r\n";}
            br.close();
            return out;
        }
        catch (IOException e) {
            System.out.println(e);
        }
        return out;
    }

    @Override
    public double parseResponse(String response) {

        String request = response;
        Pattern pattern = Pattern.compile("(\\r\\n)");
        Pattern pattern2 = Pattern.compile("<.*?>");

        String[] split = pattern.split(request);

        double returnVal = 0;
        for(int i = 0;i < split.length;i++){
            if(split[i].contains("Temperature Getter")){
                String[] split2 = pattern2.split(split[i]);
                for(int j = 0; j < split2.length;j++){
                    String currString = split2[j];
                    if(currString.length() > 0 && currString.matches("[+-]?([0-9]*[.])?[0-9]+")){
                        return Double.valueOf(currString);
                    }
                }
            }
        }
        return returnVal;
    }
}
