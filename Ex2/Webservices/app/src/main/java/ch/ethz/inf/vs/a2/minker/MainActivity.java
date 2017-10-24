package ch.ethz.inf.vs.a2.minker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.group_name);
    }

    public void startTemperatureSensorActivity(View view){
        Intent SensorActivity = new Intent(this, TemperatureSensorActivity.class);
        //SensorActivity.putExtra("SensorIndex", activeSensor);
        this.startActivity(SensorActivity);
    }

    public void startTask2Activity(View view){
        Intent task2Intent = new Intent(MainActivity.this, Task2Activity.class);
        MainActivity.this.startActivity(task2Intent);
    }

    public void startRESTServer(View view){
        Intent serverIntent = new Intent(MainActivity.this, RESTServerActivity.class);
        MainActivity.this.startActivity(serverIntent);
    }
}
