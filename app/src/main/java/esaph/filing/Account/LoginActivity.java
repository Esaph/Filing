package esaph.filing.Account;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.util.regex.Pattern;

import esaph.filing.Filing;
import esaph.filing.R;
import esaph.filing.Utils.EsaphActivity;
import esaph.filing.rechtliches.Haftungsausschluss;
import esaph.filing.workers.LoginWorkerSession;

public class LoginActivity extends EsaphActivity implements View.OnClickListener
{
    private static final int RC_SIGN_IN = 100;
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton signInButton;
    private EditText editTextFilingServerAdress;
    private CheckBox checkBoxHaftungsausschluss;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getResources().getString(R.string.filingOAuthClientId))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        TextView textViewHaftungsausschluss = findViewById(R.id.activity_login_TextViewHaftungsausschluss);

        SpannableString content = new SpannableString(textViewHaftungsausschluss.getText());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textViewHaftungsausschluss.setText(content);

        checkBoxHaftungsausschluss = findViewById(R.id.activity_login_CheckBoxHaftungsausschluss);

        textViewHaftungsausschluss.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivityForResult(new Intent(LoginActivity.this, Haftungsausschluss.class),
                        LoginActivity.resultHaftung);
            }
        });

        checkBoxHaftungsausschluss.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                shouldDisplayLoginButton();
            }
        });

        editTextFilingServerAdress = findViewById(R.id.activity_login_EditTextFilingServerAddress);
        editTextFilingServerAdress.addTextChangedListener(new TextWatcher()
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
    }

    private void shouldDisplayLoginButton()
    {
        if(validateInput(editTextFilingServerAdress.getText()) && checkBoxHaftungsausschluss.isChecked())
        {
            signInButton.setVisibility(View.VISIBLE);
        }
        else
        {
            signInButton.setVisibility(View.GONE);
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

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    private void signIn()
    {
        editTextFilingServerAdress.setEnabled(false);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private static final int resultHaftung = 561;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(editTextFilingServerAdress != null) editTextFilingServerAdress.setEnabled(true);

        if (requestCode == RC_SIGN_IN)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        else if(requestCode == LoginActivity.resultHaftung)
        {
            if(resultCode == 420)
            {
                checkBoxHaftungsausschluss.setChecked(true);
            }
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask)
    {
        try
        {
            if(editTextFilingServerAdress == null || editTextFilingServerAdress.getText().toString().isEmpty()) throw new Exception("Server adresse leer");

            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            new HPreferences(getApplicationContext()).setFilingServerAdress(editTextFilingServerAdress.getText().toString());

            new LoginWorkerSession(LoginActivity.this,
                    account)
            {
                @Override
                public void onSessionReady()
                {
                    startActivity(new Intent(LoginActivity.this, Filing.class));
                    finish();
                }

                @Override
                public void onSessionDiscorded()
                {
                    new AlertDialog.Builder(LoginActivity.this).setTitle(R.string.error_session_discorded_title)
                            .setMessage(R.string.error_session_discorded)
                            .show();
                }
            }.transfer();
        }
        catch (Exception e)
        {
            Log.i(getClass().getName(), "handleSignInResult() failed: " + e);
            Toast.makeText(LoginActivity.this, "Uupps anmeldung: " + e, Toast.LENGTH_LONG).show();
        }
    }
}