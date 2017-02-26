package com.example.etienne.folle_e;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity implements OnClickListener {
    private GoogleApiClient client;
    private Button scanBtn;
    private TextView formatTxt, contentTxt, poidsTxt;

    // Tag for logging
    private static final String TAG = "BluetoothActivity";

    // MAC address of remote Bluetooth device
    //private final String address = "30:14:10:09:16:49";//arduino
    private final String address = "B8:27:EB:1C:05:44";//rasp pi

    // The thread that does all the work
    BluetoothThread btt;

    // Handler for writing messages to the Bluetooth connection
    Handler writeHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_main);
        scanBtn = (Button)findViewById(R.id.scan_button);
        formatTxt = (TextView)findViewById(R.id.scan_format);
        contentTxt = (TextView)findViewById(R.id.scan_content);
        poidsTxt = (TextView)findViewById(R.id.scan_poids);
        scanBtn.setOnClickListener(this);
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //ajoute les entrées de menu_test à l'ActionBar
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    //gère le click sur une action de l'ActionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.co:
                if(btt != null) {
                    disconnectButtonPressed();
                }else connectButtonPressed();
                return true;
            case R.id.message:
                writeButtonPressed("1");
                return true;

        }

        return super.onOptionsItemSelected(item);
    }
    //******************************************* Bluetooth **********************************************

    public void connectButtonPressed() {
        Log.v(TAG, "Connect button pressed.");

        // Only one thread at a time
        if (btt != null) {
            Log.w(TAG, "Already connected!");
            return;
        }

        // Initialize the Bluetooth thread, passing in a MAC address
        // and a Handler that will receive incoming messages
        btt = new BluetoothThread(address, new Handler() {

            @Override
            public void handleMessage(Message message) {

                String s = (String) message.obj;

                // Do something with the message
                if (s.equals("CONNECTED")) {
                    Toast.makeText(MainActivity.this, "Connecté",
                            Toast.LENGTH_LONG).show();
                } else if (s.equals("DISCONNECTED")) {
                    Toast.makeText(MainActivity.this, "Déconnecté",
                            Toast.LENGTH_LONG).show();
                } else if (s.equals("CONNECTION FAILED")) {
                    Toast.makeText(MainActivity.this, "Erreur de connexion !",
                            Toast.LENGTH_LONG).show();
                    btt = null;
                }
            }
        });

        // Get the handler that is used to send messages
        writeHandler = btt.getWriteHandler();

        // Run the thread
        btt.start();

        Toast.makeText(MainActivity.this, "Connexion...",
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Kill the Bluetooth thread.
     */
    public void disconnectButtonPressed() {
        Log.v(TAG, "Disconnect button pressed.");

        if(btt != null) {
            btt.interrupt();
            btt = null;
        }
    }

    public void writeButtonPressed(String m) {
        Log.v(TAG, "Write button pressed.");

        Message msg = Message.obtain();
        msg.obj = m;
        writeHandler.sendMessage(msg);
    }

    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }


    //******************************************* Fin Bluetooth *******************************************

    @Override
    public void onStart() {
        super.onStart();

        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();


    }
    public void onClick(View v){

        if(v.getId()==R.id.scan_button) {
            //IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            new com.google.zxing.integration.android.IntentIntegrator(this).initiateScan();
            System.out.println("deuxieme ok");
            //scanIntegrator.initiateScan();
//scan
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
//retrieve scan result

        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            Produit ontest = new Produit();
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();
            ImageView imageView;
            try {
                ontest.nomme(scanContent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            formatTxt.setText("Nom du produit : " + ontest.Nom);
            poidsTxt.setText("Poids : " + ontest.Poids);
            contentTxt.setText("Code Barre : " + scanContent);
            imageView = (ImageView) findViewById(R.id.imageView);
            Picasso.with(getBaseContext()).load(ontest.Photo).into(imageView);
            //On envoie un message au caddie
            if (btt != null) {
                writeButtonPressed(ontest.Poids);
            }
            //we have a result
        }

        else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
