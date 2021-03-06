package ch.ethz.inf.vs.a1.minker.antitheft;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity{

    private boolean prefEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty);

        prefEnabled = this.getIntent().getBooleanExtra("prefEnabled", prefEnabled);

        /*
        Button btn = findViewById(R.id.confirm_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.sharedprefs) , Context.MODE_PRIVATE).edit();

                EditText sens = findViewById(R.id.editText3);
                if (!sens.getText().toString().equals("")) {
                    int sensitivity = Integer.parseInt(sens.getText().toString());
                    editor.putInt("sensitivity", sensitivity);
                }

                EditText del = findViewById(R.id.editText2);
                if(!del.getText().toString().equals("")) {
                    float delay = Float.parseFloat(del.getText().toString());
                    editor.putFloat("delay", delay);
                }

                editor.apply();
                toast("The changes will be applied once you restart the service");
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.sharedprefs) , Context.MODE_PRIVATE);
        float delay = sharedPreferences.getFloat("delay", AntiTheftService.DEFAULT_DELAY);
        int sensitivity = sharedPreferences.getInt("sensitivity", AntiTheftService.DEFAULT_SENSITIVITY);
        ((EditText) findViewById(R.id.editText2)).setHint("current: "+delay);
        ((EditText) findViewById(R.id.editText3)).setHint("current: "+sensitivity); */


        Bundle args = new Bundle();
        args.putBoolean("prefEnabled", prefEnabled);
        MySettingsFragment f = new MySettingsFragment();
        f.setArguments(args);
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, f)
                .commit();

    }

    private void toast(String msg){
        Context context = getApplicationContext();
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.show();
    }
}
