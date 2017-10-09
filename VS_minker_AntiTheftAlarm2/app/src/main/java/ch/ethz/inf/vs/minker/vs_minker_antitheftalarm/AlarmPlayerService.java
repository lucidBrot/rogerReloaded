package ch.ethz.inf.vs.minker.vs_minker_antitheftalarm;
// Don't need this service, because once we start the alarm, we can keep the AntiTheftService busy making music.
// This AlarmPlayerService here doesn't really work anyways.

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class AlarmPlayerService extends Service{

    private static MediaPlayer mediaPlayer_alarm;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID){
        boolean start = intent.getBooleanExtra("start", false);
        Log.d("AlarmPlayerService", "AlarmPlayerService was created");
        if(start) {
            mediaPlayer_alarm = MediaPlayer.create(MainActivity.appcontext, R.raw.alarm);
            setMPVolume(100); //set to max volume
            mediaPlayer_alarm.setLooping(true);
            mediaPlayer_alarm.start();
            toast("starting Alarm Music");
        } else {
            if(mediaPlayer_alarm.isPlaying()) {
                mediaPlayer_alarm.stop();
            } else {
                Log.d("AlarmPlayerService","Media player was not playing... weird" );
            }
        }
        return START_STICKY;
    }

    private void toast(String msg){
        Context context = getApplicationContext();
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void setMPVolume(int volume){
        float i = (float) volume/100;
        mediaPlayer_alarm.setVolume((float) i, (float) i);
    }

    @Override
    public void onDestroy(){
        mediaPlayer_alarm.stop();
        mediaPlayer_alarm.release();
    }
}
