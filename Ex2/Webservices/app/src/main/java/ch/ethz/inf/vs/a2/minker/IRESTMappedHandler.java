package ch.ethz.inf.vs.a2.minker;

import java.util.Map;

/**
 * Created by Josua on 10/24/17.
 */

public interface IRESTMappedHandler {
    String handle(Map<String, String> request, Map<String, String> header, Map<String, String> param);
}
