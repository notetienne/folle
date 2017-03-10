package com.example.etienne.folle_e;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

public class Accueil extends AppCompatActivity {

    Button btngo;
    ImageView Cmc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);

        final Animation an = AnimationUtils.loadAnimation(getBaseContext(),R.anim.cmc_anim);
        btngo = (Button) findViewById(R.id.btngo);
        Cmc = (ImageView) findViewById(R.id.cmc);
        Cmc.startAnimation(an);

        btngo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(R.anim.transition_d, R.anim.transition_g);
                finish();
            }
        });
    }
}
