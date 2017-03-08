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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, InfosFrag.OnFragmentInteractionListener, ArticleAdapter.ProduitAdapterListener {

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
    InfosFrag mInfosFrag;
    ArticleAdapter adapter;
    public List<Produit> listeprod;
    TextView DisplayPrix;

    //****************** variables Scan ****************
    private GoogleApiClient client;
    private Button scanBtn;
    private TextView formatTxt, contentTxt, poidsTxt;
    private Button caisse;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("ok");
        //Scan
        listeprod = new ArrayList<Produit>();
        mListView = (ListView) findViewById(R.id.liste);
        //On met à jour l'affichage du nombre d'article
        mInfosFrag = (InfosFrag) getSupportFragmentManager().findFragmentById(R.id.info_frag);
        mInfosFrag.NbArticles(listeprod.size());
        //mListView = liste du XML. listeprod = liste des produits mise à jour à chaque scan. On affiche donc "listeprod" via mListView
        adapter = new ArticleAdapter(MainActivity.this, listeprod);
        adapter.addListeners(this);
        mListView.setAdapter(adapter);
        DisplayPrix = (TextView)findViewById(R.id.display_prix);
        //Lancer le Bluetooth
        connectButtonPressed();

        //Créé un listener pour le bouton scan
        scanBtn = (Button) findViewById(R.id.scan_button);
        scanBtn.setOnClickListener(this);

        //bouton pour le payement
        caisse = (Button) findViewById(R.id.caisse);
        caisse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Caisse.class);
                startActivity(intent);
            }
        });

        //Utile pour le scan
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

    }

    //Implémerter cette méthode pour récupérer des infos du fragment
    @Override
    public void onFragmentInteraction(Uri uri) {
        return;
    }

    //******************************* Bluetooth *******************************

    //Lancer la connexion Bluetooth
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


    //Déconnecte le Bluetooth -> Kill the Bluetooth thread.
    public void disconnectButtonPressed() {
        Log.v(TAG, "Disconnect button pressed.");

        if(btt != null) {
            writeButtonPressed("quit");
            btt.interrupt();
            btt = null;
        }
    }
    //Envoyer un message via Bluetooth
    public void writeButtonPressed(String m) {
        Log.v(TAG, "Write button pressed.");

        Message msg = Message.obtain();
        msg.obj = m;
        writeHandler.sendMessage(msg);
    }

//******************************* Scan *******************************

    //Au click du bouton scan
    public void onClick(View v){

        if(v.getId()==R.id.scan_button) {
            IntentIntegrator scanIntegrator = new IntentIntegrator(MainActivity.this);
            scanIntegrator.initiateScan();
        }
    }

    //Gestion du scan + récupération des infos sur le produit scanné
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            int i = listeprod.size();
            Produit produitencours = new Produit();
            String scanContent = scanningResult.getContents();
            try {
                produitencours.nomme(scanContent);
            } catch (JSONException e) {
                System.out.println("erreur de CB");
                e.printStackTrace();
            }
            //Si le produit existe dans la bdd, l'ajouter, sinon afficher un message d'erreur
            if(produitencours.Nom != null){
                listeprod.add(produitencours);
                //Affichage de la liste
                mListView.setAdapter(adapter);
                //mise à jour affichage du nombre d'articles et total
                System.out.println("maj nb produits");
                mInfosFrag.NbArticles(listeprod.size());
                CalculSomme();
            }else{
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Produit introuvable", Toast.LENGTH_SHORT);
                toast.show();
            }
            //Si on est connecté au caddie, lui envoyer le poids
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

   public void CalculSomme(){
       int i = 0;
       float sum = 0;
       System.out.println("début calcul somme");
       for (i = 0; i< listeprod.size(); i++){
           sum += listeprod.get(i).Prix;
           System.out.println(listeprod.get(i).Prix);
       }
       DisplayPrix.setText("Total : " + sum + "€");
    }
    public void onClickNom(Produit item, int position) {
        Intent intent = new Intent(getApplicationContext(),InfosProduits.class);
        intent.putExtra("Nom", listeprod.get(position).Nom);
        intent.putExtra("Poids", listeprod.get(position).Poids);
        intent.putExtra("Prix", Float.toString(listeprod.get(position).Prix));
        intent.putExtra("URLImage", listeprod.get(position).Photo);
        startActivity(intent);
    }
}
