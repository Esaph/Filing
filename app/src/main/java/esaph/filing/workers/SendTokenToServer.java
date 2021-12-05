package esaph.filing.workers;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.SoftReference;

import esaph.elib.esaphcommunicationservices.EsaphCommunicationClient;
import esaph.elib.esaphcommunicationservices.EsaphPipe;
import esaph.elib.esaphcommunicationservices.EsaphSocketCenter;
import esaph.elib.esaphcommunicationservices.Session;
import esaph.filing.Utils.SocketConfig;

public class SendTokenToServer extends EsaphCommunicationClient
{
    private SoftReference<Context> contextWeakReference;
    private SoftReference<TokenListener> tokenListenerSoftReference;
    private String fcmToken;

    public SendTokenToServer(Context context, String fcmToken, TokenListener tokenListener)
    {
        this.contextWeakReference = new SoftReference<>(context);
        this.tokenListenerSoftReference = new SoftReference<>(tokenListener);
        this.fcmToken = fcmToken;
    }

    public interface TokenListener
    {
        void onTokenRegistered(boolean success);
    }

    @Override
    public JSONObject bindData() throws JSONException
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("FT", fcmToken);
        return jsonObject;
    }

    @Override
    public EsaphSocketCenter.EsaphSocketCenterConfiguration requestSocketConfiguration() throws Exception
    {
        return SocketConfig.defaultConfig(contextWeakReference.get());
    }

    private boolean success = false;
    @Override
    public void onPipeReady(EsaphPipe esaphPipe)
    {
        try
        {
            JSONObject reply = new JSONObject(esaphPipe.getBufferedReader().readLine());
            success = reply.getBoolean("SUCCESS");
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "failed: " + ec);
        }
        finally
        {
            runUI(new Runnable()
            {
                @Override
                public void run()
                {
                    TokenListener tokenListener = tokenListenerSoftReference.get();
                    if(tokenListener != null)
                    {
                        tokenListener.onTokenRegistered(success);
                    }
                }
            });
        }
    }

    @Override
    public void onLibraryError(Exception e)
    {
        runUI(new Runnable()
        {
            @Override
            public void run()
            {
                TokenListener tokenListener = tokenListenerSoftReference.get();
                if(tokenListener != null)
                {
                    tokenListener.onTokenRegistered(false);
                }
            }
        });
    }

    @Override
    public void onShowLoading()
    {
    }

    @Override
    public void onHideLoading()
    {
    }

    @Override
    public String getPipeCommand()
    {
        return "UFT";
    }

    @Override
    public Session getSession() //No need Session for this event.
    {
        return null;
    }
}
