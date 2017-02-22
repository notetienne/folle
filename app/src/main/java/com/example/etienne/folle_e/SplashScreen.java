package com.example.etienne.folle_e;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);

        final ImageView chariot = (ImageView) findViewById(R.id.chariot);
        final Animation an = AnimationUtils.loadAnimation(getBaseContext(),R.anim.translate);
        //final Animation an2 = AnimationUtils.loadAnimation(getBaseContext(),R.anim.abc_fade_out);

        Thread myThread = new Thread(){
            @Override
            public void run() {
                try {
                    chariot.startAnimation(an);
                    sleep(1500);
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        myThread.start();
    }
}
