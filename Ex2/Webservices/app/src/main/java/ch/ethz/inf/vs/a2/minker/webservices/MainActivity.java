package ch.ethz.inf.vs.a2.minker.webservices;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // start activity of task2
        findViewById(R.id.main_button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent task2Intent = new Intent(MainActivity.this, Task2Activity.class);
                MainActivity.this.startActivity(task2Intent);
            }
        });
    }


}
