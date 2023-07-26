package esaph.filing;
// TODO: 08.11.2019 filling dataaccess card

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import esaph.filing.Account.HPreferences;
import esaph.filing.Account.LoginActivity;
import esaph.filing.Board.BoardFragment;
import esaph.filing.Board.BoardsAnzeigen.ActivityBoardPicker;
import esaph.filing.Board.ShowBoardContent.Model.BoardListe;
import esaph.filing.CardsShowingFromList.Model.Auftrag.Auftrag;
import esaph.filing.KarteErstellen.KarteErstellen;
import esaph.filing.TourenPlaner.TourenPlaner;
import esaph.filing.Utils.EsaphActivity;
import esaph.filing.Utils.EsaphDialog;
import esaph.filing.logging.LogFragment;
import esaph.filing.workers.DeleteListe;
import esaph.filing.workers.LoginWorkerSession;
import esaph.filing.workers.TransferAuftrag;

public class Filing extends EsaphActivity implements DeleteListe.DeleteListeListener,
        TransferAuftrag.TransferAutragListener
{
    private GoogleSignInClient mGoogleSignInClient;
    private static int CURRENT_PAGE_ID = R.id.menuBoard;
    private Fragment fragmentCurrentBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filing);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getResources().getString(R.string.filingOAuthClientIdReleaseAndDebug))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mGoogleSignInClient.silentSignIn()
                .addOnCompleteListener(
                        this,
                        new OnCompleteListener<GoogleSignInAccount>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<GoogleSignInAccount> task)
                            {
                                handleSignInResult(task);
                            }
                        });

        try
        {
            HPreferences hPreferences = new HPreferences(getApplicationContext());
            if(hPreferences.getWorkingBench() == null)
            {
                Intent intent = new Intent(Filing.this, ActivityBoardPicker.class);
                startActivityForResult(intent, Filing.REQUEST_CODE_BOARD_CHOOSE);
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "No board selected check exception: " + ec);
        }
    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask)
    {
        try
        {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String ID_TOKEN = account.getIdToken();

            new LoginWorkerSession(Filing.this,
                    account)
            {
                @Override
                public void onSessionReady()
                {
                    startApplication();
                }

                @Override
                public void onSessionDiscorded()
                {
                    new AlertDialog.Builder(Filing.this).setTitle(R.string.error_session_discorded_title)
                            .setMessage(R.string.error_session_discorded)
                            .show();
                }
            }.transfer();

            checkRightsPermission(false);
        }
        catch (ApiException e)
        {
            startActivity(new Intent(Filing.this, LoginActivity.class));
            finish();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        checkRightsPermission(true);
    }


    private BottomNavigationView bottomNavigationView;
    private void startApplication()
    {
        if(esaphDialog != null)
        {
            esaphDialog.dismiss();
            esaphDialog = null;
        }

        bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.activity_filling_bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item)
                    {
                        Fragment selectedFragment = null;
                        switch (item.getItemId())
                        {
                            case R.id.menuTour:
                                selectedFragment = TourenPlaner.getInstance();
                                break;

                            case R.id.menuBoard:
                                selectedFragment = BoardFragment.getInstance();
                                break;

                            case R.id.menuKarteErstellen:
                                Intent intent = new Intent(Filing.this, KarteErstellen.class);
                                startActivityForResult(intent,
                                        Filing.requestCodeCreateKarte);
                                break;

                            case R.id.menuLog:
                                selectedFragment = LogFragment.getInstance();
                                break;
                        }

                        if(selectedFragment == null || item.getItemId() == R.id.menuKarteErstellen) return false;

                        Filing.CURRENT_PAGE_ID = item.getItemId();

                        fragmentCurrentBottom = selectedFragment;

                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.activityFiling_Framelayout_Container, selectedFragment)
                                .commitAllowingStateLoss(); //Stupid bugs from Google.
                        return true;
                    }
                });

        bottomNavigationView.setSelectedItemId(Filing.CURRENT_PAGE_ID);
    }


    private static final int REQUEST_CODE_REQUEST_PERMISSION = 420;
    private void checkRightsPermission(boolean preventInfinte)
    {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            if (ContextCompat.checkSelfPermission(Filing.this, Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED)
            {
                if(!preventInfinte)
                {
                    ActivityCompat.requestPermissions(Filing.this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            Filing.REQUEST_CODE_REQUEST_PERMISSION);
                }
            }
        }
    }

    private EsaphDialog esaphDialog;
    private void handlePermissionsNotGranted()
    {
        esaphDialog = EsaphDialog.EsaphDialogBuilder.getInstance()
                .setTitle(getResources().getString(R.string.dialog_alert_title_NoPermission))
                .setDetail(getResources().getString(R.string.dialog_alert_title_NoPermission_Text))
                .setmButtonTextPositiv(getResources().getString(R.string.einstellungenOpen)).request(Filing.this);

        esaphDialog.setCanceledOnTouchOutside(false);
        esaphDialog.setCancelable(false);
        esaphDialog.getButtonPositive().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openPermissionSettings(Filing.this);
            }
        });
    }

    public static void openPermissionSettings(Activity activity)
    {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + activity.getPackageName()));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case Filing.REQUEST_CODE_REQUEST_PERMISSION:

                boolean hasPermissions = true;

                if(grantResults.length > 0)
                {
                    for (int grantResult :
                            grantResults)
                    {
                        if(grantResult == PackageManager.PERMISSION_DENIED)
                        {
                            hasPermissions = false;
                            break;
                        }
                    }
                }
                else
                {
                    hasPermissions = false;
                }



                //Granted permission

                if(hasPermissions)
                {
                    startApplication();
                }
                else
                {
                    handlePermissionsNotGranted();
                }

                break;
        }
    }

    private static final int REQUEST_CODE_BOARD_CHOOSE = 1004;
    private static final int requestCodeCreateKarte = 526;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    { //Must be there, to dispatch event to fragments.
        super.onActivityResult(requestCode, resultCode, data);
        if(fragmentCurrentBottom != null)
        {
            fragmentCurrentBottom.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onResult(BoardListe boardListe, boolean success)
    {
        if(fragmentCurrentBottom instanceof BoardFragment)
        {
            BoardFragment boardFragment = (BoardFragment) fragmentCurrentBottom;
            boardFragment.onResult(boardListe, success);
        }
    }

    @Override
    public void onResult(Auftrag auftrag, long boardListeCurrent, long boardListeTransfered, boolean success)
    {
        if(fragmentCurrentBottom instanceof BoardFragment)
        {
            BoardFragment boardFragment = (BoardFragment) fragmentCurrentBottom;
            boardFragment.onResult(auftrag, boardListeCurrent,
                    boardListeTransfered,
                    success);
        }
    }
}
