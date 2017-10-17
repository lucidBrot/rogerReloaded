package ch.ethz.inf.vs.a2.minker.webservices;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class XmlSensor extends ch.ethz.inf.vs.a2.sensor.AbstractSensor {

    private String serviceAddress;
    private String serviceAction;

    /**
     * set some default values for testing. Use {@link XmlSensor()} with parameters for real usage.
     */
    public XmlSensor(){
        this("http://vslab.inf.ethz.ch:8080/SunSPOTWebServices/SunSPOTWebservice", "http://webservices.vslecture.vs.inf.ethz.ch/SayHello/SayHelloRequest");
    }

    public XmlSensor(String serviceAddress, String serviceAction){
        this.serviceAddress = serviceAddress;
        this.serviceAction = serviceAction;
    }
    /**
     * Executes a request to fetch the temperature value.
     *
     * @return Response (not parsed yet)
     * @throws Exception Any exception that could happen during the request
     *
     * Code mostly taken from https://stackoverflow.com/a/38409010/2550406
     */
    @Override
    public String executeRequest() throws Exception {
        String response = "";
        BufferedReader reader = null;
        HttpURLConnection conn = null;
        try{
            URL urlObj = new URL(serviceAddress);
            conn = (HttpURLConnection) urlObj.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true); // as recommended by the exercise
            OutputStreamWriter вр = new OutputStreamWriter(conn.getOutputStream());
            String data = ""; //TODO: load data
            // possibly helpful links:
            /*
            https://stackoverflow.com/a/2560129/2550406 <--- building XML into string
            http://www.webservicex.net/uszip.asmx?op=GetInfoByCity <-- some example webservice
            https://stackoverflow.com/a/19745299/2550406 <-- using SOAP library

            http://vslab.inf.ethz.ch:8080/SunSPOTWebServices/SayHello?wsdl <-- Example WSDL
            http://vslab.inf.ethz.ch:8080/SunSPOTWebServices/SunSPOTWebservice <-- webservice overview
            http://vslab.inf.ethz.ch:8080/SunSPOTWebServices/SunSPOTWebservice?Tester <-- Tester
            http://vslab.inf.ethz.ch:8080/SunSPOTWebServices/SunSPOTWebservice?wsdl <-- WSDL I need to use
             */

            вр.write(data);
            вр.flush();
            Log.d("executeRequest", "post response code: " + conn.getResponseCode());

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine())!=null){
                stringBuilder.append(line).append("\n");
            }

            response = stringBuilder.toString();
        } catch (Exception e){
            Log.e("executeRequest", "Caught Exception : "+e.getMessage());
            throw(e);
        } finally {
            try{
                if(reader!=null) {
                    reader.close();
                }
                if(conn!=null){
                    conn.disconnect();
                }
            } catch (Exception e){
                Log.e("executeRequest", "Caught YET ANOTHER Exception");
                throw(e);
            }
        }
        return response;
    }

    /**
     * Parse response that has been returned after sending the request.
     *
     * @param response Raw response
     * @return Parsed sensor value or {@link Double#NaN} if parsing fails
     */
    @Override
    public double parseResponse(String response) {
        return 0;
    }

}
