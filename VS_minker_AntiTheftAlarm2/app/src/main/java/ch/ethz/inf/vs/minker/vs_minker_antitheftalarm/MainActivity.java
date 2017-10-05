package ch.ethz.inf.vs.minker.vs_minker_antitheftalarm;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int sensitivity = 100;

        SpikeMovementDetector spikeMovementDetector = new SpikeMovementDetector(new AlarmCallback() {
            @Override
            public void onDelayStarted() {
                toast("Callback worked");
            }
        }, sensitivity);

        spikeMovementDetector.doAlarmLogic(null);
    }

    private void toast(String msg){
        Context context = getApplicationContext();
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.show();
    }
}
