package com.example.etienne.folle_e;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class Caisse extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.etienne.folle_e.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caisse);
    }
    public void ConnexionClient (View view) {
        Intent intent = new Intent(this, CaisseConnect.class);
        EditText mailclient = (EditText) findViewById(R.id.mailclient);
        EditText passclient = (EditText) findViewById(R.id.passwordclient);
        String mailclientstring = mailclient.getText().toString();
        String passclientstring = passclient.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, mailclientstring);
        intent.putExtra(EXTRA_MESSAGE, passclientstring);
        startActivity(intent);
    }
}
