package ch.ethz.inf.vs.a2.minker;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Josua on 10/24/17.
 */

public class RESTUrlMapper {

    private Map<String, IRESTMappedHandler> mapper = new HashMap<>();

    public RESTUrlMapper(){

    }

    public String dispatchUrl(Map<String, String> request, Map<String, String> header,  Map<String, String> data) throws RESTHttpException{
        IRESTMappedHandler handler = mapper.get(request.get("uri"));
        if(handler != null){
            return handler.handle(request, header, data);
        }
        //TODO: Abort 404
        throw new RESTHttpException(RESTHttpStatus.NOT_FOUND, "Not Found");
    }

    public void registerHandler(String path, IRESTMappedHandler handler){
        if(!mapper.containsKey(path) && handler != null){
            mapper.put(path, handler);
        }
    }

}
