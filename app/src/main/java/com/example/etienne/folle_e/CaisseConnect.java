package com.example.etienne.folle_e;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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
        final String mailclientstring = intent.getStringExtra(Caisse.EXTRA_MAIL);
        final String passclientstring = intent.getStringExtra(Caisse.EXTRA_PASS);
        final String Total = intent.getStringExtra("Total");
        final float Totalfloat = Float.valueOf(Total);
        final float credit;
        Button payerbutt;

        String url = "http://195.154.170.113/folleweb/connexion.php?mail=" + mailclientstring + "&password=" + passclientstring;
        HttpHandler sh = new HttpHandler();
        String jsonStr = sh.makeServiceCall(url);
        System.out.println(jsonStr);
        if (!jsonStr.contains("null")) {
            try {
                JSONArray mainJson = new JSONArray(jsonStr);
                JSONObject client = mainJson.getJSONObject(0);
                String mail = client.getString("mail");
                credit = Float.valueOf(client.getString("credit"));
                TextView textView = (TextView) findViewById(R.id.textView3);
                textView.setText("Votre solde restant s'élève à : " + credit + "€ \n \n Vous devez payer : " + Totalfloat + "€");

                payerbutt = (Button) findViewById(R.id.Payer);
                payerbutt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        payer(mailclientstring, passclientstring, Totalfloat, credit);
                        view.setVisibility(View.GONE);
                        /** Intent intent = new Intent(getApplicationContext(),Accueil.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        overridePendingTransition(R.anim.transition_d, R.anim.transition_g);
                        finish(); **/
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }


        } else {
            TextView textView = (TextView) findViewById(R.id.textView3);
            textView.setText("Pas de compte à ce mail");
        }



    }
    public void payer (String mailclientstring, String passclientstring, float addition, float actuelfloat){
        String url = "http://195.154.170.113/folleweb/paiement.php?mail="+ mailclientstring +"&password=" + passclientstring +"&total=" + addition+ "+&actuel=" + actuelfloat;
        System.out.println("lien ok");
        HttpHandler sh = new HttpHandler();
        String reussite = sh.makeServiceCall(url);
        TextView textView = (TextView) findViewById(R.id.paiementstatus);
        textView.setText(reussite);
    }
}
