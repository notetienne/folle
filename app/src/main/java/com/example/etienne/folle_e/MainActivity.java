package com.example.etienne.folle_e;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.Thing;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements InfosFrag.OnFragmentInteractionListener {
    //****************** variables Bluetooth *******************
    // Tag for logging
    private static final String TAG = "BluetoothActivity";

    // MAC address of remote Bluetooth device
    //private final String address = "30:14:10:09:16:49";//arduino
    private final String address = "B8:27:EB:1C:05:44";//rasp pi

    // The thread that does all the work
    BluetoothThread btt;

    // Handler for writing messages to the Bluetooth connection
    Handler writeHandler;

    //****************** variables liste *******************

    ListView mListView;
    List<String> liste = new ArrayList<String>();
    InfosFrag mInfosFrag;


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

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, liste);
        mListView.setAdapter(adapter);

        connectButtonPressed();
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

}
