package ch.ethz.inf.vs.a2.minker;

import android.util.Log;
import ch.ethz.inf.vs.a2.minker.sensor.AbstractSensor;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.net.Proxy;

import static org.ksoap2.serialization.MarshalHashtable.NAMESPACE;

public class SoapSensor extends AbstractSensor {

    String NAMESPACE = "http://webservices.vslecture.vs.inf.ethz.ch/";
    String MAIN_REQUEST_URL ="http://vslab.inf.ethz.ch:8080/SunSPOTWebServices/SunSPOTWebservice?xsd=1";
    String SOAP_ACTION = "http://webservices.vslecture.vs.inf.ethz.ch/SunSPOTWebservice/getSpotRequest";

    @Override
    public String executeRequest() throws Exception {

        String methodname = "getSpot";
        SoapObject request = new SoapObject(NAMESPACE, methodname);
        request.addProperty("id", "Spot3");
        SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
        HttpTransportSE ht = getHttpTransportSE();
        ht.call(SOAP_ACTION, envelope);

        SoapObject so = (SoapObject) envelope.getResponse();
        Log.d("Task2/SoapSensor", "response: "+so.toString());

        return null;
    }

    // Code by Sashen Govender
    // https://code.tutsplus.com/tutorials/consuming-web-services-with-ksoap--mobile-21242
    private final SoapSerializationEnvelope getSoapSerializationEnvelope(SoapObject request) {
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.implicitTypes = true;
        envelope.setAddAdornments(false);
        envelope.setOutputSoapObject(request);

        return envelope;
    }
    private final HttpTransportSE getHttpTransportSE() {
        HttpTransportSE ht = new HttpTransportSE(Proxy.NO_PROXY,MAIN_REQUEST_URL,60000);
        ht.debug = true;
        ht.setXmlVersionTag("<!--?xml version=\"1.0\" encoding= \"UTF-8\" ?-->");
        return ht;
    }

    @Override
    public double parseResponse(String response) {

        return 0;
    }
}
