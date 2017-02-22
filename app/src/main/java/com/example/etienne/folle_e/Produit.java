package com.example.etienne.folle_e;

import android.graphics.Bitmap;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import static com.google.android.gms.wearable.DataMap.TAG;

public class Produit {
    String Nom=null;
    String Type=null;
    String Poids=null;
    String Photo=null;
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
            this.Nom="erreur dans le json ej sais pas quoi";
        }

    }
}
