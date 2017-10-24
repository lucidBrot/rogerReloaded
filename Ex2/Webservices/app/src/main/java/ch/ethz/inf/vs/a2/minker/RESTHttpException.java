package ch.ethz.inf.vs.a2.minker;

/**
 * Created by Josua on 10/24/17.
 */

public class RESTHttpException extends Exception {
    private final RESTHttpStatus status;

    public RESTHttpException(RESTHttpStatus status, String message){
        super(message);
        this.status = status;
    }

    public RESTHttpException(RESTHttpStatus status, String message, Exception e){
        super(message, e);
        this.status = status;
    }

    public RESTHttpStatus getStatus(){
        return status;
    }
}
