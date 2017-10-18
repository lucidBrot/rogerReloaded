package ch.ethz.inf.vs.a2.minker.http;

/**
 * Created by Chris on 17.10.2017.
 */

public class HttpRawRequestImpl implements HttpRawRequest {
    @Override
    public String generateRequest(String host, int port, String path) {

        String carriageReturn = "\r\n"; //define carriage return and newline in this String for convenience

        String returnString = "";
        returnString += "GET " + path + " HTTP/1.1" + carriageReturn;
        returnString += "Host: " + host + ":" + port + carriageReturn;
        returnString += "Accept: " + "text/html" + carriageReturn;
        returnString += "Connection: " + "close" + carriageReturn;
        returnString += carriageReturn; //close header

        return returnString;
    }
}
