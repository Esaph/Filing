package esaph.filing.workers;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.SoftReference;

import esaph.elib.esaphcommunicationservices.EsaphCommunicationClient;
import esaph.elib.esaphcommunicationservices.EsaphPipe;
import esaph.elib.esaphcommunicationservices.EsaphSocketCenter;
import esaph.elib.esaphcommunicationservices.Session;
import esaph.filing.Account.User;
import esaph.filing.R;
import esaph.filing.Utils.SocketConfig;

public abstract class LoginWorkerSession extends EsaphCommunicationClient
{
    private SoftReference<Context> contextWeakReference;
    private GoogleSignInAccount googleSignInAccount;

    public LoginWorkerSession(Context context,
                              GoogleSignInAccount googleSignInAccount)
    {
        this.contextWeakReference = new SoftReference<>(context);
        this.googleSignInAccount = googleSignInAccount;
    }

    @Override
    public JSONObject bindData() throws JSONException
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("CLID", contextWeakReference.get().getResources().getString(R.string.filingOAuthClientIdReleaseAndDebug));
        jsonObject.put("idTS", googleSignInAccount.getIdToken());
        return jsonObject;
    }

    @Override
    public EsaphSocketCenter.EsaphSocketCenterConfiguration requestSocketConfiguration() throws Exception
    {
        return SocketConfig.defaultConfig(contextWeakReference.get());
    }

    @Override
    public void onPipeReady(EsaphPipe esaphPipe)
    {
        try
        {
            JSONObject reply = new JSONObject(esaphPipe.getBufferedReader().readLine());
            Session session = new Session().createSelfFromMap(reply.getJSONObject("DATA").toString());

            if(!TextUtils.isEmpty(session.getmSession()))
            {
                SessionHolder.setSession(session, new User(googleSignInAccount.getId(),
                        googleSignInAccount.getDisplayName(),
                        googleSignInAccount.getGivenName(),
                        googleSignInAccount.getFamilyName(),
                        googleSignInAccount.getEmail(),
                        ""));

                runUI(new Runnable() {
                    @Override
                    public void run()
                    {
                        onSessionReady();
                    }
                });
            }
            else
            {
                runUI(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        onSessionDiscorded();
                    }
                });
            }
        }
        catch (Exception e)
        {
            Log.i(getClass().getName(), "failed: " + e);
            runUI(new Runnable()
            {
                @Override
                public void run()
                {
                    onSessionDiscorded();
                }
            });
        }
    }

    public abstract void onSessionReady();
    public abstract void onSessionDiscorded();

    @Override
    public String getPipeCommand() {
        return "LWG";
    }

    @Override
    public Session getSession()
    {
        return null; //Session must be null.
    }

    public static class SessionHolder
    {
        private static Session session;
        private static User userAccountLogged;
        private static boolean isSessionValid = false;
        private static final Object mLock = new Object();

        public static boolean isIsSessionValid()
        {
            synchronized (mLock)
            {
                return isSessionValid;
            }
        }

        public static void setSession(Session session,
                                      User userAccountLogged)
        {
            SessionHolder.isSessionValid = true;
            synchronized (SessionHolder.mLock)
            {
                SessionHolder.session = session;
                SessionHolder.userAccountLogged = userAccountLogged;
                SessionHolder.mLock.notifyAll();
            }
        }

        public static User getLoggedUser()
        {
            synchronized (SessionHolder.mLock)
            {

                while(!SessionHolder.isSessionValid)
                {
                    try
                    {
                        SessionHolder.mLock.wait();
                    }
                    catch (Exception ec)
                    {
                        Log.i("SessionHolder", "getLoggedUser() failed: " + ec);
                    }
                }

                return SessionHolder.userAccountLogged;
            }
        }


        public static Session getSession()
        {
            synchronized (SessionHolder.mLock)
            {
                while(!SessionHolder.isSessionValid)
                {
                    try
                    {
                        SessionHolder.mLock.wait();
                    }
                    catch (Exception ec)
                    {
                    }
                }

                return SessionHolder.session;
            }
        }
    }

    private ProgressDialog progressDialog;
    @Override
    public void onShowLoading()
    {
        // TODO: 25.11.2019 prevent leaks window
        Context context = contextWeakReference.get();
        if(context != null)
        {
            progressDialog = ProgressDialog.show(context,
                    context.getResources().getString(R.string.loadingTitle),
                    "", true);
            progressDialog.show();
        }
    }

    @Override
    public void onHideLoading()
    {
        if(progressDialog != null)
        {
            progressDialog.hide();
        }
    }

    @Override
    public void onLibraryError(Exception e)
    {
        System.out.println("LIBRARY ERROR: " + e);
        runUI(new Runnable() {
            @Override
            public void run() {
                onSessionDiscorded();
            }
        });
    }
}