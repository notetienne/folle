package com.example.etienne.folle_e;

import android.graphics.drawable.Drawable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;

public class Produit {
    String Nom=null;
    String Type=null;
    String Poids=null;
    String Photo=null;
    String Prix = null;
    int  color;
    int code=0;

    public Produit() {
    }



    public void test(String lenom) {
        this.Nom=lenom;
    }
    public void nomme(String barcode) throws JSONException {
        String url = "http://195.154.170.113/folleweb/barcode.php?code="+ barcode;
        HttpHandler sh = new HttpHandler();
        System.out.println("handler ok");
        String jsonStr = sh.makeServiceCall(url);
        System.out.println("json string ok");
        if (jsonStr!=null) {
            try {
                System.out.println("before array");
                JSONArray mainJson = new JSONArray(jsonStr);
                System.out.println(barcode);
                System.out.println("json objé");
                JSONObject product = mainJson.getJSONObject(0);
                String nomfr = product.getString("name");
                String poidsfr = product.getString("poids");
                String urlphoto = product.getString("photo");
                System.out.println(nomfr);
                this.Nom = nomfr;
                this.Poids = poidsfr;
                this.Photo = urlphoto;
                this.Prix = product.getString("prix");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            /** try {
                JSONArray arr = new JSONArray(jsonStr);
                Log.e(TAG, "Response from url: " + jsonStr);
                JSONObject jObj = arr.getJSONObject(0);
                this.Nom = jObj.getString("product_name_fr");
                System.out.println(this.Nom);
                this.Type = "rien";
                this.code = 0;
            } catch (JSONException e) {
                e.printStackTrace();
            } **/
        }
        else {
            this.Nom="erreur dans le json je sais pas quoi";
        }

    }

    public String getNom(){
        if(this.Nom !=null){
            return this.Nom;
        }else return "null";
    }

    public String getPoids(){
        if(this.Poids != null){
            return this.Poids + "g";
        }else return "null";
    }

    public String getPrix(){
        if(this.Prix != null){
            return this.Prix + " €";
        }else return "null";
    }

    public Drawable getImage(){
       //return Color.GRAY;
        //return Picasso.with(getBaseContext()).load(this.Photo);
        //return this.Photo;

        try {
            URL urle = new URL(this.Photo);
            Object content = urle.getContent();

            InputStream inputstream = (InputStream)content;
            Drawable drawable = Drawable.createFromStream(inputstream, "src");
            return drawable;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
