package com.example.etienne.folle_e;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CaisseConnect extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caisseconnect);
        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String mailclientstring = intent.getStringExtra(Caisse.EXTRA_MAIL);
        String passclientstring = intent.getStringExtra(Caisse.EXTRA_PASS);

        String url = "http://195.154.170.113/folleweb/connexion.php?mail="+ mailclientstring +"&password=" + passclientstring;
        HttpHandler sh = new HttpHandler();
        String jsonStr = sh.makeServiceCall(url);
        if (jsonStr!=null) {
            try {
                JSONArray mainJson = new JSONArray(jsonStr);
                JSONObject client = mainJson.getJSONObject(0);
                String mail = client.getString("mail");
                String credit = client.getString("credit");
                TextView textView = (TextView) findViewById(R.id.textView3);
                textView.setText("Votre credit restant : " + credit);
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
            TextView textView = (TextView) findViewById(R.id.textView3);
            textView.setText("Pas de compte à ce mail");
        }



    }
}
