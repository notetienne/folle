package com.example.etienne.folle_e;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import static com.google.android.gms.wearable.DataMap.TAG;

public class Produit {
    String Nom=null;
    String Type=null;
    int code=0;

    public Produit() {
    }

    public void test(String lenom) {
        this.Nom=lenom;
    }
    public void nomme() throws JSONException {
        String url = "http://fr.openfoodfacts.org/api/v0/produit/5410041424805";
        HttpHandler sh = new HttpHandler();
        String jsonStr = sh.makeServiceCall(url);
        if (jsonStr!=null) {
            try {
                JSONArray arr = new JSONArray(jsonStr);
                Log.e(TAG, "Response from url: " + jsonStr);
                JSONObject jObj = arr.getJSONObject(0);
                this.Nom = jObj.getString("product_name_fr");
                this.Type = "rien";
                this.code = 0;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            this.Nom="erreur dans le json ej sais pas quoi";
        }

    }
}
