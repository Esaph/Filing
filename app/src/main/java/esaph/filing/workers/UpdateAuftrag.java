package esaph.filing.workers;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.SoftReference;

import esaph.elib.esaphcommunicationservices.EsaphCommunicationClient;
import esaph.elib.esaphcommunicationservices.EsaphPipe;
import esaph.elib.esaphcommunicationservices.EsaphSocketCenter;
import esaph.elib.esaphcommunicationservices.Session;
import esaph.filing.Board.ShowBoardContent.Model.BoardListe;
import esaph.filing.CardsShowingFromList.Model.Auftrag.Auftrag;
import esaph.filing.R;
import esaph.filing.Utils.SocketConfig;

public class UpdateAuftrag extends EsaphCommunicationClient
{
    private SoftReference<Context> contextWeakReference;
    private SoftReference<UpdateAuftragListener> updateAuftragListenerWeakReference;
    private Auftrag auftragKarte;
    private BoardListe boardListe;

    public UpdateAuftrag(Context context,
                         Auftrag auftragKarte,
                         BoardListe boardListe,
                         UpdateAuftragListener updateAuftragListener)
    {
        this.contextWeakReference = new SoftReference<>(context);
        this.updateAuftragListenerWeakReference = new SoftReference<>(updateAuftragListener);
        this.auftragKarte = auftragKarte;
        this.boardListe = boardListe;
    }

    public interface UpdateAuftragListener
    {
        void onResult(Auftrag auftragKarte, boolean success);
    }

    @Override
    public JSONObject bindData() throws JsonProcessingException, JSONException
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("BOARDL", boardListe.getObjectMapJson(boardListe));
        jsonObject.put("AT", auftragKarte.getObjectMapJson(auftragKarte));
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
            auftragKarte = new Auftrag().createSelfFromMap(reply.getJSONObject("DATA").toString());
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
                    UpdateAuftragListener updateAuftragListener = updateAuftragListenerWeakReference.get();
                    if(updateAuftragListener != null)
                    {
                        updateAuftragListener.onResult(auftragKarte, success);
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
                UpdateAuftragListener updateAuftragListener = updateAuftragListenerWeakReference.get();
                if(updateAuftragListener != null)
                {
                    updateAuftragListener.onResult(auftragKarte, false);
                }
            }
        });
    }

    private ProgressDialog progressDialog;
    @Override
    public void onShowLoading()
    {
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
    public String getPipeCommand()
    {
        return "UA";
    }

    @Override
    public Session getSession()
    {
        return LoginWorkerSession.SessionHolder.getSession();
    }
}