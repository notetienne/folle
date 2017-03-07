package com.example.etienne.folle_e;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, InfosFrag.OnFragmentInteractionListener {
    //****************** variables Bluetooth *******************
    // Tag for logging
    private static final String TAG = "BluetoothActivity";

    // MAC address of remote Bluetooth device
    private final String address = "30:14:10:09:16:49";//arduino
    //private final String address = "B8:27:EB:1C:05:44";//rasp pi

    // The thread that does all the work
    BluetoothThread btt;

    // Handler for writing messages to the Bluetooth connection
    Handler writeHandler;

    //****************** variables liste *******************

    ListView mListView;
    List<String> liste = new ArrayList<String>();
    InfosFrag mInfosFrag;
    ArrayAdapter<String> adapter;

    //****************** Scan ****************
    private GoogleApiClient client;
    private Button scanBtn;
    private TextView formatTxt, contentTxt, poidsTxt;


    private Button caisse;

    public ArrayList<Produit> listeprod = new ArrayList<Produit>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("ok");

        mListView = (ListView) findViewById(R.id.liste);
        liste.add("fromage");
        liste.add("Oeufs");
        liste.add("pates");
        liste.add("jambon");
        liste.add("poulet");

        mInfosFrag = (InfosFrag) getSupportFragmentManager().findFragmentById(R.id.info_frag);
        mInfosFrag.NbArticles(liste.size());

        adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, liste);
        mListView.setAdapter(adapter);

        connectButtonPressed();

        scanBtn = (Button) findViewById(R.id.scan_button);
        scanBtn.setOnClickListener(this);

        caisse = (Button) findViewById(R.id.caisse);
        caisse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Caisse.class);
                startActivity(intent);
            }
        });

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        /*scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("scan ici");
            }
        });*/

    }


    @Override
    public void onFragmentInteraction(Uri uri) {
        return;
    }

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
                    mInfosFrag.EtatCo("Connecté");
                } else if (s.equals("DISCONNECTED")) {
                    mInfosFrag.EtatCo("Déconnecté");
                } else if (s.equals("CONNECTION FAILED")) {
                    mInfosFrag.EtatCo("Erreur de connexion !");
                    btt = null;
                }
            }
        });

        // Get the handler that is used to send messages
        writeHandler = btt.getWriteHandler();

        // Run the thread
        btt.start();

        mInfosFrag.EtatCo("Connexion au caddie...");
    }

    /**
     * Kill the Bluetooth thread.
     */
    public void disconnectButtonPressed() {
        Log.v(TAG, "Disconnect button pressed.");

        if(btt != null) {
            writeButtonPressed("quit");
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

//******************************* Scan

    public void onClick(View v){

        if(v.getId()==R.id.scan_button) {
            IntentIntegrator scanIntegrator = new IntentIntegrator(MainActivity.this);
            scanIntegrator.initiateScan();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            int i = listeprod.size();
            Produit produitencours = new Produit();
            listeprod.add(produitencours);
            String scanContent = scanningResult.getContents();
            try {
                listeprod.get(i).nomme(scanContent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            liste.add(listeprod.get(i).Nom);
            mListView.setAdapter(adapter);
            if (btt != null) {
                writeButtonPressed(listeprod.get(i).Poids);
            }
        }

        else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
