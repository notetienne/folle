package com.example.etienne.folle_e;

import android.graphics.drawable.Drawable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
@SuppressWarnings("serial")
public class Produit implements Serializable {
    String Nom=null;
    String Type=null;
    String Poids=null;
    String Photo=null;
    Float Prix = null;
    String Compo=null;
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
                this.Prix = Float.parseFloat(product.getString("prix"));
                String url2 = "http://fr.openfoodfacts.org/api/v0/produit/"+ barcode +".json";
                HttpHandler sh2 = new HttpHandler();
                String jsonStr2 = sh2.makeServiceCall(url2);
                if (jsonStr2!=null) {
                    try {
                        JSONObject mainJson2 = new JSONObject(jsonStr2);
                        JSONObject product2 = mainJson2.getJSONObject("product");
                        this.Compo = product2.getString("ingredients_text_with_allergens_fr");
                        System.out.println(this.Compo);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    this.Compo="inconnu";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        else {
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
            String msg = this.Prix + " €";
            return msg;
        }else return "null";
    }

    public Drawable getImage(){
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
