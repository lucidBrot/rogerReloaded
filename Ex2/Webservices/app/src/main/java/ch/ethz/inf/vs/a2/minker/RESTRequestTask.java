package ch.ethz.inf.vs.a2.minker;

import android.os.AsyncTask;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;


/**
 * Created by Josua on 10/23/17.
 */

class RESTRequestTask extends AsyncTask<Socket, Void, Void> {

    private final static String TASK_TAG = "RESTTask";
    private final RESTServerService service;

    private final static String internalServerError = "HTTP/1.1 500 Internal Server Error\r\nContent-Length: 71\r\n" +
            "Content-Type: text/html\r\nConnection: Closed\r\n\r\n" +
            "<html><head></head><body><h1>Internal server Error</h1></body></html>\r\n";


    RESTRequestTask(RESTServerService service){
        super();
        this.service = service;
    }

    @Override
    protected Void doInBackground(Socket... sockets) {
        if (sockets == null || sockets.length == 0){
            Log.e(TASK_TAG,"No Sockets to Handle. Returning...");
            return null;
        }
        if(sockets.length > 1){
            Log.i(TASK_TAG,"Too many sockets. Use only the first one");
        }
        Socket socket = sockets[0];


        Map<String, String> request = new HashMap<>();
        Map<String, String> header = new HashMap<>();
        Map<String, String> data = new HashMap<>();
        try {
            parseRequest(new BufferedReader(new InputStreamReader(socket.getInputStream())), request, header, data);
        } catch (IOException e) {
            e.printStackTrace();
            writeResponse(socket, generateErrorMessage(RESTHttpStatus.INTERNAL_ERROR));
            return null;
        } catch (RESTHttpException e){
            writeResponse(socket, generateErrorMessage(e.getStatus()));
            return null;
        }
        String response = dispatch(request, header, data);

        writeResponse(socket,response);

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Code inspired by NanoHTTPd
     */
    private void parseRequest(BufferedReader in, Map<String, String> requestout,  Map<String, String> headerOut, Map<String, String> data) throws RESTHttpException{
        try {
            String inLine = in.readLine();
            if (inLine == null) {
                return;
            }
            StringTokenizer st = new StringTokenizer(inLine);

            if (!st.hasMoreTokens()) {
                throw new RESTHttpException(RESTHttpStatus.BAD_REQUEST, "Request ist badly formed");
            }

            requestout.put("method", st.nextToken());
            if (!st.hasMoreTokens()) {
                throw new RESTHttpException(RESTHttpStatus.BAD_REQUEST, "Request ist badly formed");
            }
            String uri = st.nextToken();


            String protocolVersion;
            if (st.hasMoreTokens()) {
                protocolVersion = st.nextToken();
            } else {
                protocolVersion = "HTTP/1.1";
            }
            requestout.put("protocol", protocolVersion);

            int getParamStart = uri.indexOf("?");
            if(getParamStart >= 0) {
                uri = uri.substring(0, getParamStart);
            }
            uri = URLDecoder.decode(uri, "UTF8");

            //ignore trailing /
            if (uri.charAt(uri.length() - 1) != '/'){
                uri += "/";
            }
            requestout.put("uri", uri);

            String line = in.readLine();
            while (line != null && !line.trim().isEmpty()){
                int separator = line.indexOf(":");
                if(separator >= 0){
                    headerOut.put(line.substring(0,separator).trim().toLowerCase(),line.substring(separator+1).trim());
                }
                line = in.readLine();
            }


        } catch (IOException e){
            Log.e(TASK_TAG, "Internal Error: IOException: " + e.getMessage());
            throw new RESTHttpException(RESTHttpStatus.INTERNAL_ERROR, "ERROR: " + e.getMessage());
        }

    }

    private String dispatch(Map<String, String> request, Map<String, String> headerArgs, Map<String, String> data){
        if(request.get("uri") == null){
            return "";
        }
        String response;
        try {
            String responseBody = service.getUrlMapper().dispatchUrl(request, headerArgs, data);
            response = generateResponseHeader(responseBody);
            service.logToActivity("200 " + request.get("method") + " " + request.get("uri"));

        } catch (RESTHttpException e) {
            response = generateErrorMessage(e.getStatus());

            service.logToActivity(e.getStatus().getRequestStatus() + " " + request.get("method") +
                    " " + request.get("uri"));

        } catch (Exception e){
            e.printStackTrace();

            String responseBody = "<html><head><title>" + RESTHttpStatus.INTERNAL_ERROR.getDescription()
                    + "</title></head><body><h1>" + RESTHttpStatus.INTERNAL_ERROR.getDescription() +
                    "</h1></body></html>";
            response = generateResponseHeader(responseBody,  RESTHttpStatus.INTERNAL_ERROR);

            service.logToActivity(RESTHttpStatus.INTERNAL_ERROR.getRequestStatus() + " " + request.get("method")
                    + " " + request.get("uri"));
        }

        return response;
    }

    private String generateResponseHeader(String message){
        return  generateResponseHeader(message, RESTHttpStatus.OK);
    }

    private String generateResponseHeader(String message, RESTHttpStatus status){
        String responseHeader = "HTTP/1.1 " + status.getDescription() + "\r\nContent-Type: text/html\r\nConnection: Closed\r\n";
        int messageLength = message.length();
        responseHeader += "Content-Length: " + messageLength;
        responseHeader += "\r\n\r\n";
        return  responseHeader + message;
    }

    private void writeResponse(Socket socket, String response){
        try {
            PrintWriter writer = new PrintWriter(socket.getOutputStream(),true);
            writer.print(response);
            writer.flush();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String generateErrorMessage(RESTHttpStatus status){
        String responseBody = "<html><head><title>" + status.getDescription() +
                "</title></head><body><h1>" + status.getDescription() + "</h1></body></html>";;

        return generateResponseHeader(responseBody, status);
    }
}

