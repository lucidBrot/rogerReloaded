package ch.ethz.inf.vs.a3.vsminkerchat;

import android.os.AsyncTask;
import android.util.Log;

/**
 * AsyncTask Task1Registrator. The background task returns true if registration was successful after this.tries retries
 */
public class Task1Registrator extends AsyncTask<Void, Void, Boolean> {
    private int tries;

    /**
     * Generate a Task1Registrator AsyncTask
     * @param serverIP
     * @param serverPort
     */
    Task1Registrator(String serverIP, int serverPort, int tries){
        super();
        this.tries = tries;

    }

    /**
     * Generate a Task1Registrator with default values 10.0.2.2:4446, 5 tries to connect
     */
    Task1Registrator(){
        this("10.0.2.2",4446,5);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        Log.d("Task1/Registrator", "was called asynchronously");
        return null;
    }
}
