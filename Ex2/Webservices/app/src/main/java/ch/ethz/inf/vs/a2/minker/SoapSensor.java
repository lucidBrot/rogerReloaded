package ch.ethz.inf.vs.a2.minker;

import android.util.Log;
import ch.ethz.inf.vs.a2.minker.sensor.AbstractSensor;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.net.Proxy;

import static org.ksoap2.serialization.MarshalHashtable.NAMESPACE;

public class SoapSensor extends AbstractSensor {

    @Override
    public String executeRequest() throws Exception {
        // code taken from Neeraj Mishra : https://www.thecrazyprogrammer.com/2016/11/android-soap-client-example-using-ksoap2.html
        // Strings are hardcoded because the interface does not allow to pass them as parameters
        String result = "";

        String NAMESPACE = "http://webservices.vslecture.vs.inf.ethz.ch/";
        String METHOD_NAME = "getSpot";
        SoapObject soapObject = new SoapObject(NAMESPACE, METHOD_NAME);

        PropertyInfo propertyInfo = new PropertyInfo();
        String PARAMETER_NAME = "id";
        propertyInfo.setName(PARAMETER_NAME);
        String PARAM = "Spot3";
        propertyInfo.setValue(PARAM);
        propertyInfo.setType(String.class);

        soapObject.addProperty(propertyInfo);

        SoapSerializationEnvelope envelope =  new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(soapObject);

        String URL = "http://vslab.inf.ethz.ch:8080/SunSPOTWebServices/SunSPOTWebservice?wsdl";
        HttpTransportSE httpTransportSE = new HttpTransportSE(URL);

        try {
            String SOAP_ACTION = "http://webservices.vslecture.vs.inf.ethz.ch/SunSPOTWebservice/getSpot";
            httpTransportSE.call(SOAP_ACTION, envelope);
            SoapObject soapObj = (SoapObject)envelope.getResponse();
            result = soapObj.getPrimitivePropertyAsString("temperature");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public double parseResponse(String response) {
        try {
            return Double.valueOf(response);
        } catch (NumberFormatException e){
            Log.e("Task2/SoapSensor", "Invalid double: "+response);
            return Double.NaN;
        }
    }
}
