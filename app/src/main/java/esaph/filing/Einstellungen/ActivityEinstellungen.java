package esaph.filing.Einstellungen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import java.util.regex.Pattern;

import esaph.filing.Account.HPreferences;
import esaph.filing.MainActivity;
import esaph.filing.R;
import esaph.filing.Utils.EsaphActivity;

public class ActivityEinstellungen extends EsaphActivity
{
    private static final int RESTUL_CODE_LOGGED_OUT = 100;
    private TextView textViewBenutzername;
    private TextView textViewAbmelden;
    private EditText editTextFilingServer;
    private TextView textViewBack;
    private Button buttonSpeichern;
    private String filingServerAdress;

    public ActivityEinstellungen()
    {
        // Required empty public constructor
    }

    public static ActivityEinstellungen getInstance()
    {
        return new ActivityEinstellungen();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_einstellungen);

        HPreferences hPreferences = new HPreferences(getApplicationContext());
        try
        {
            filingServerAdress = hPreferences.getFilingServerAdress() + ":" + hPreferences.getFilingServerPort();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        textViewBack = findViewById(R.id.activity_einstellungen_TextView_back);
        textViewBenutzername = findViewById(R.id.fragment_einstellungen_TextViewBenutzername);
        textViewAbmelden = findViewById(R.id.fragment_einstellungen_ButtonAbmelden);
        editTextFilingServer = findViewById(R.id.fragment_einstellungen_EditTextFilingAdresse);
        buttonSpeichern = findViewById(R.id.fragment_einstellungen_ButtonCheck);

        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(ActivityEinstellungen.this);
        if(account != null)
        {
            textViewBenutzername.setText(account.getDisplayName());
        }

        textViewAbmelden.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getResources().getString(R.string.filingOAuthClientId))
                        .requestEmail()
                        .build();


                final ProgressDialog progressDialog = ProgressDialog.show(ActivityEinstellungen.this,
                        getResources().getString(R.string.loadingTitle),
                        "", true);
                progressDialog.show();

                GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(ActivityEinstellungen.this, gso);
                mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(!isFinishing())
                        {
                            progressDialog.dismiss();

                            HPreferences hPreferences = new HPreferences(getApplicationContext());
                            hPreferences.logOut();
                            Intent intent = new Intent(ActivityEinstellungen.this, MainActivity.class);
                            startActivity(intent);

                            setResult(ActivityEinstellungen.RESTUL_CODE_LOGGED_OUT, new Intent());
                            finish();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        if(!isFinishing())
                        {
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        });

        editTextFilingServer.setText(filingServerAdress != null ? filingServerAdress : "");

        editTextFilingServer.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                shouldDisplayLoginButton();
            }

            @Override
            public void afterTextChanged(Editable s)
            {
            }
        });

        textViewBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }


    private void shouldDisplayLoginButton()
    {
        if(validateInput(editTextFilingServer.getText()))
        {
            buttonSpeichern.setVisibility(View.VISIBLE);
        }
        else
        {
            buttonSpeichern.setVisibility(View.GONE);
        }
    }

    private Pattern p = Pattern.compile("^"
            + "(((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}" // Domain name
            + "|"
            + "localhost" // localhost
            + "|"
            + "(([0-9]{1,3}\\.){3})[0-9]{1,3})" // Ip
            + ":"
            + "[0-9]{1,5}$"); // Port

    private boolean validateInput(CharSequence ip)
    {
        return p.matcher(ip).matches();
    }

}