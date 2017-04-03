package com.example.etienne.folle_e;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, InfosFrag.OnFragmentInteractionListener, InsertCaddieFrag.OnFragmentInteractionListener {

    //****************** variables Bluetooth *******************
    // Tag for logging
    private static final String TAG = "BluetoothActivity";

    // MAC address of remote Bluetooth device
    private final String address = "30:14:10:09:16:49";//arduino
    //private final String address = "B8:27:EB:1C:05:44";//rasp pi

    // The thread that does all the work
    BluetoothThread btt;

    // Handler for writing messages to the Bluetooth connection
    public Handler writeHandler;
    TextView EtatCo;

    //****************** variables liste *******************

    ListView mListView;
    ArticleAdapter adapter;
    public static List<Produit> listeprod;
    TextView DisplayPrix;
    TextView DisplayNb;
    float sum = 0;
    String FileName = "myfile";

    static int PoidsCaddie = 0;
    static boolean Connexion = false;
    static Produit produitencours;
    RelativeLayout RlInfoFrag;

    //****************** variables Scan ****************
    private GoogleApiClient client;
    private Button scanBtn;
    private TextView formatTxt, contentTxt, poidsTxt;
    private Button caisse;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("ok");

        setContentView(R.layout.activity_main);
        RlInfoFrag = (RelativeLayout) findViewById(R.id.rl_info);
        final Animation an = AnimationUtils.loadAnimation(getBaseContext(),R.anim.rl_info_anim);
        RlInfoFrag.startAnimation(an);
        //Scan
        listeprod = new ArrayList<Produit>();
        //On récupère la liste de produits stockés si elle existe

        //RecupListe();

        mListView = (ListView) findViewById(R.id.liste);
        EtatCo = (TextView) findViewById(R.id.etat_co);

        //mListView = liste du XML. listeprod = liste des produits mise à jour à chaque scan. On affiche donc "listeprod" via mListView
        adapter = new ArticleAdapter(MainActivity.this, listeprod);
        //mListView.setAdapter(adapter);
        DisplayPrix = (TextView)findViewById(R.id.display_prix);
        DisplayNb = (TextView)findViewById(R.id.display_nb);
        //On met à jour l'affichage du nombre d'article
        //DisplayNb.setText(listeprod.size() + " articles");
        //Listener pour clique sur article
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView,
                                    View view,
                                    int position,
                                    long id) {
                onClickNom(position);
            }
        });


        //Lancer le Bluetooth
        if(! Connexion)  connectButtonPressed();

        //Créé un listener pour le bouton scan
        scanBtn = (Button) findViewById(R.id.scan_button);
        scanBtn.setOnClickListener(this);
        if(! Connexion){
            scanBtn.setVisibility(View.GONE);
        }else EtatCo.setText("Connecté");
        //bouton pour le payement
        caisse = (Button) findViewById(R.id.caisse);
        caisse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeButtonPressed("end");
                Intent intent = new Intent(getApplicationContext(),Caisse.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("Total", String.valueOf(sum));
                startActivity(intent);
            }
        });

        //Utile pour le scan
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mListView.setAdapter(adapter);
        DisplayNb.setText(listeprod.size() + " articles");
        CalculSomme();
    }

    @Override
    protected void onDestroy() {
        if (btt != null) {
            writeButtonPressed("quit");
        }
        //On stock la liste des produits sur le téléphone
        try {
            FileOutputStream fos = openFileOutput(FileName, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(listeprod);
            fos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }

    //Implémerter cette méthode pour récupérer des infos du fragment
    @Override
    public void onFragmentInteraction(Uri uri) {
        return;
    }

    @Override
    public void onAnnul(Uri uri) {

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
                    Connexion = true;
                    EtatCo.setText("Connecté");
                    Animation an2 = AnimationUtils.loadAnimation(getBaseContext(),R.anim.scan_btn_anim);
                    scanBtn.startAnimation(an2);
                    scanBtn.setVisibility(View.VISIBLE);

                } else if (s.equals("DISCONNECTED")) {
                    EtatCo.setText("Déconnecté");
                } else if (s.equals("CONNECTION FAILED")) {
                    connectButtonPressed();
                    if(! Connexion) EtatCo.setText("Erreur de connexion !");
                    btt = null;
                } else {
                    //PoidsCaddie = Integer.parseInt(s);
                    try{
                        PoidsCaddie = Integer.parseInt(s.trim());
                    }catch (NumberFormatException e){
                        System.out.println("ERREUR DANS LA CONVERTION DU POIDS");
                    }
                }
            }
        });

        // Get the handler that is used to send messages
        writeHandler = btt.getWriteHandler();

        // Run the thread
        btt.start();


        if(! Connexion) EtatCo.setText("Connexion au caddie...");
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
            produitencours = new Produit();
            String scanContent = scanningResult.getContents();
            try {
                produitencours.nomme(scanContent);
            } catch (JSONException e) {
                System.out.println("erreur de CB");
                e.printStackTrace();
            }
            //Si le produit existe dans la bdd, l'ajouter, sinon afficher un message d'erreur
            if(produitencours.Nom != null){
                writeButtonPressed("5");
                //if(PoidsCaddie < 100) while (!(PoidsCaddie >= 100));
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Poids : " + PoidsCaddie, Toast.LENGTH_LONG);
                toast.show();
                listeprod.add(produitencours);
                //Affichage de la liste
                mListView.setAdapter(adapter);
                //mise à jour affichage du nombre d'articles et total
                DisplayNb.setText(listeprod.size() + " articles");
                CalculSomme();
            }/*else{
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Produit introuvable", Toast.LENGTH_SHORT);
                toast.show();
            }*/

        }

        else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

   public void CalculSomme(){
       int i = 0;
       sum = 0;
       System.out.println("début calcul somme");
       for (i = 0; i< listeprod.size(); i++){
           sum += listeprod.get(i).Prix;
           System.out.println(listeprod.get(i).Prix);
       }
       DisplayPrix.setText("Total : " + sum + "€");
    }

    public void onClickNom(int position) {
        Intent intent = new Intent(getApplicationContext(),InfosProduits.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("pos", position);
        startActivity(intent);
        overridePendingTransition(R.anim.transition_d, R.anim.transition_g);
    }


    public void RecupListe() {
        /*Runnable runnable = new Runnable() {
            @Override
            public void run() {

                try {
                    FileInputStream fis = openFileInput(FileName);
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    listeprod = (ArrayList<Produit>) ois.readObject();

                    fis.close() ;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mListView.setAdapter(adapter);
                DisplayNb.setText(listeprod.size() + " articles");
                CalculSomme();;
            }
        };
        new Thread(runnable).start();*/
        try {
            FileInputStream fis = openFileInput(FileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            listeprod = (ArrayList<Produit>) ois.readObject();

            fis.close() ;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
