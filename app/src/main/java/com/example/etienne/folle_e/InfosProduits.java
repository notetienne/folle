package com.example.etienne.folle_e;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;

public class InfosProduits extends AppCompatActivity implements InfosFrag.OnFragmentInteractionListener,Serializable {

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infos_produits);

        TextView DisplayNom = (TextView) findViewById(R.id.nom_article);
        TextView DisplayPoids = (TextView) findViewById(R.id.poids_article);
        TextView DisplayPrix = (TextView) findViewById(R.id.prix_article);
        TextView DisplayCompo = (TextView) findViewById(R.id.composition);
        ImageView avatar = (ImageView) findViewById(R.id.avatar);
        Button Delete = (Button) findViewById(R.id.delete);

        Intent i = getIntent();
        final int position = i.getIntExtra("pos", 0);
        String nom = MainActivity.listeprod.get(position).Nom;
        String prix = Float.toString(MainActivity.listeprod.get(position).Prix);
        final String poids = MainActivity.listeprod.get(position).Poids;
        String URLImage = MainActivity.listeprod.get(position).Photo;
        String compo = MainActivity.listeprod.get(position).Compo;

        avatar.setImageDrawable(getImage(URLImage));
        DisplayNom.setText(nom);
        DisplayPrix.setText(prix + " €");
        DisplayPoids.setText(poids + "g");
        DisplayCompo.setText("Composition : "+ compo);


        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                //startActivity(intent);
                MainActivity.listeprod.remove(position);
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
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 1:
                Context context= this;
                dialog=new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_dialog);
                Button restart=(Button)dialog.findViewById(R.id.restart);
                restart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
//whatever code you want to execute on restart
                    }
                });
                break;
            default: break;
        }
        return dialog;
    }


    @Override
    public void onFragmentInteraction(Uri uri) {
        return;
    }

}
