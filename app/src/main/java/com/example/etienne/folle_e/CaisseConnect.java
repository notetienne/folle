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

        Intent intent = getIntent();
        String mailclientstring = intent.getStringExtra(Caisse.EXTRA_MAIL);
        String passclientstring = intent.getStringExtra(Caisse.EXTRA_PASS);
        String Total = intent.getStringExtra("Total");
        Float Totalfloat = Float.valueOf(Total);

        String url = "http://195.154.170.113/folleweb/connexion.php?mail="+ mailclientstring +"&password=" + passclientstring;
        HttpHandler sh = new HttpHandler();
        String jsonStr = sh.makeServiceCall(url);
        System.out.println(jsonStr);
        if (!jsonStr.contains("null")) {
            try {
                JSONArray mainJson = new JSONArray(jsonStr);
                JSONObject client = mainJson.getJSONObject(0);
                String mail = client.getString("mail");
                String credit = client.getString("credit");
                TextView textView = (TextView) findViewById(R.id.textView3);
                textView.setText("Votre solde restant s'élève à : " + credit + "€, vous devez payer" + Totalfloat + "€");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            payer(mailclientstring, passclientstring, Totalfloat);
        }

        else {
            TextView textView = (TextView) findViewById(R.id.textView3);
            textView.setText("Pas de compte à ce mail");
        }

    }
    public void payer(String mailclientstring, String passclientstring, float addition){
        String url = "http://195.154.170.113/folleweb/paiement.php?mail="+ mailclientstring +"&password=" + passclientstring +"&total=" + addition;
        HttpHandler sh = new HttpHandler();
        String reussite = sh.makeServiceCall(url);
        TextView textView = (TextView) findViewById(R.id.paiementstatus);
        textView.setText(reussite);
    }
}
