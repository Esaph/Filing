package esaph.filing;

/*
 * Copyright (c) 2023.
 *  Julian Auguscik
 */

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.messaging.FirebaseMessaging;

import esaph.filing.Account.HPreferences;
import esaph.filing.Account.LoginActivity;
import esaph.filing.Board.ShowBoardContent.Model.BoardListe;
import esaph.filing.CardsShowingFromList.Model.Auftrag.Auftrag;
import esaph.filing.CardsShowingFromList.Model.Auftrag.ViewType;
import esaph.filing.Utils.EsaphActivity;
import esaph.filing.workers.CreateNewAuftrag;

public class MainActivity extends EsaphActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
    }


    private void performTest()
    {
        BoardListe boardListe = new HPreferences(getApplicationContext()).getWorkingList();

        for(int count = 0; count < 8; count++)
        {
            Auftrag auftrag = new Auftrag(ViewType.AUFTRAG_KARTE,
                    0,
                    "",
                    System.currentTimeMillis() + 100*1000,
                    Color.RED,
                    0,
                    "Count",
                    "Testing aufgaben beschreibung: " + count);

            new CreateNewAuftrag(MainActivity.this,
                    auftrag,
                    boardListe,
                    new CreateNewAuftrag.CreateNewAuftragListener()
                    {
                        @Override
                        public void onResult(Auftrag auftragKarte, boolean success)
                        {
                            if(success)
                            {
                                Log.i(getClass().getName(), "Success added Auftrag: " + success);
                            }
                        }
                    }).transfer();
        }
    }





    @Override
    protected void onStart()
    {
        super.onStart();
        HPreferences hPreferences = new HPreferences(getApplicationContext());
        try
        {
            if(hPreferences.getFilingServerAdress().isEmpty())
            {
                hPreferences.logOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                return;
            }

            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            if(account != null)
            {
                startActivity(new Intent(MainActivity.this, Filing.class));
                finish();
            }
            else
            {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "onStart() failed: " + ec);
            hPreferences.logOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
    }
}
