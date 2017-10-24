package ch.ethz.inf.vs.a2.minker;

import android.support.annotation.Nullable;
import android.util.Log;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
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
            conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true); // as recommended by the exercise
            OutputStreamWriter вр = new OutputStreamWriter(conn.getOutputStream());
            String data = buildRequest("getSpot3"); // TODO: Actually get temperature
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
            Log.d("Task2/executeRequest", "post response code: " + conn.getResponseCode());

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine())!=null){
                stringBuilder.append(line).append("\n");
            }

            response = stringBuilder.toString();
        } catch (Exception e){
            Log.e("Task2/executeRequest", "Caught Exception : "+e.getMessage());
            e.printStackTrace();
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
                Log.e("Task2/executeRequest", "Caught YET ANOTHER Exception");
                throw(e);
            }
        }
        return response;
    }


    /**
     * @param name can be <i>getDiscoveredSpots</i>
     * @return an XML Request or null
     */
    @Nullable
    private String buildRequest(String name) {
        StringBuilder sb = new StringBuilder();
        switch(name){
            case "getDiscoveredSpots":
            {
                sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\">");
                sb.append("<S:Header/>");
                sb.append("<S:Body>");
                sb.append("<ns2:getDiscoveredSpots xmlns:ns2=\"http://webservices.vslecture.vs.inf.ethz.ch/\"/>");
                sb.append("</S:Body>");
                sb.append("</S:Envelope>");
                return sb.toString();
            }
            case "getSpot3":
            {
                return
                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                                + "<S:Header/><S:Body>"
                                +"<ns2:getSpot xmlns:ns2=\"http://webservices.vslecture.vs.inf.ethz.ch/\">"
                                +"<id>Spot3</id>"
                                +"</ns2:getSpot>"
                                +"</S:Body>"
                                +"</S:Envelope>";
            }
            case "getSpot4":
            {
                return
                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                                + "<S:Header/><S:Body>"
                                +"<ns2:getSpot xmlns:ns2=\"http://webservices.vslecture.vs.inf.ethz.ch/\">"
                                +"<id>Spot4</id>"
                                +"</ns2:getSpot>"
                                +"</S:Body>"
                                +"</S:Envelope>";
            }
            default:
                return null;
        }
    }

    /**
     * Parse response that has been returned after sending the request.
     *
     * @param response Raw response
     * @return Parsed sensor value or {@link Double#NaN} if parsing fails
     */
    @Override
    public double parseResponse(String response) {

        Log.d("Task2/executeRequest", "response was "+response);

        XmlPullParserFactory factory = null;
        try {
            factory = XmlPullParserFactory.newInstance();
        } catch (XmlPullParserException e) {
            Log.e("Task2/executeRequest", "Failed to instantiate XmlPullParserFactory");
            e.printStackTrace();
            return Double.NaN;
        }
        factory.setNamespaceAware(true);
        XmlPullParser xpp = null;
        try {
            xpp = factory.newPullParser();
        } catch (XmlPullParserException e) {
            Log.e("Task2/executeRequest", "Failed to instantiate PullParser");
            e.printStackTrace();
            return Double.NaN;
        }

        try {
            xpp.setInput( new StringReader( response ) );
        } catch (XmlPullParserException e) {
            Log.e("Task2/executeRequest", "failed to set Input for Parser");
            e.printStackTrace();
            return Double.NaN;
        }
        int eventType = 0;
        double temperature;
        try {
            eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if(eventType == XmlPullParser.START_DOCUMENT) {
                    Log.d("Task2/Parser","Start document");
                } else if(eventType == XmlPullParser.START_TAG) {
                    Log.d("Task2/Parser", "Start tag "+xpp.getName());
                    if (xpp.getName().equals("temperature")){
                        temperature = Double.parseDouble(xpp.nextText());
                        return temperature;
                    }
                } else if(eventType == XmlPullParser.END_TAG) {
                    Log.d("Task2/Parser", "End tag "+xpp.getName());
                } else if(eventType == XmlPullParser.TEXT) {
                    Log.d("Task2/Parser", "Text "+xpp.getText());
                }
                eventType = xpp.next();
            }
        } catch (Exception e) {
            Log.e("Task2/executeRequest", "Failed attempt of Parsing");
            e.printStackTrace();
            return Double.NaN;
        }
        return Double.NaN;
    }

}
