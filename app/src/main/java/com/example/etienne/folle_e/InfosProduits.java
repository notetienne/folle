package com.example.etienne.folle_e;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;

public class InfosProduits extends AppCompatActivity implements InfosFrag.OnFragmentInteractionListener,Serializable {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infos_produits);

        TextView DisplayNom = (TextView) findViewById(R.id.nom_article);
        TextView DisplayPoids = (TextView) findViewById(R.id.poids_article);
        TextView DisplayPrix = (TextView) findViewById(R.id.prix_article);
        Button Delete = (Button) findViewById(R.id.delete);
        Intent i = getIntent();
        Produit pro = (Produit)i.getSerializableExtra("art");

        String nom = i.getStringExtra("Nom");
        String prix = i.getStringExtra("Prix");
        String poids = i.getStringExtra("Poids");
        String URLImage = i.getStringExtra("URLImage");
        ImageView avatar = (ImageView) findViewById(R.id.avatar);

        avatar.setImageDrawable(getImage(URLImage));
        DisplayNom.setText(nom);
        DisplayPrix.setText(prix + " €");
        DisplayPoids.setText(poids + "g");


        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                //startActivity(intent);
                finish();
            }
        });


    }

    public Drawable getImage(String url){
        try {
            URL urle = new URL(url);
            Object content = urle.getContent();

            InputStream inputstream = (InputStream)content;
            Drawable drawable = Drawable.createFromStream(inputstream, "src");
            return drawable;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    @Override
    public void onFragmentInteraction(Uri uri) {
        return;
    }

}
