package esaph.filing.workers;

/*
 * Copyright (c) 2023.
 *  Julian Auguscik
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.ProgressBar;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.SoftReference;

import esaph.elib.esaphcommunicationservices.EsaphCommunicationClient;
import esaph.elib.esaphcommunicationservices.EsaphPipe;
import esaph.elib.esaphcommunicationservices.EsaphSocketCenter;
import esaph.elib.esaphcommunicationservices.Session;
import esaph.filing.CardsShowingFromList.Model.Auftrag.Auftrag;
import esaph.filing.Board.ShowBoardContent.Model.BoardListe;
import esaph.filing.R;
import esaph.filing.Utils.GlobalBroadCasts;
import esaph.filing.Utils.SocketConfig;

public class CreateNewAuftrag extends EsaphCommunicationClient
{
    private SoftReference<Context> contextWeakReference;
    private SoftReference<CreateNewAuftragListener> createNewAuftragListenerWeakReference;
    private Auftrag auftragKarte;
    private BoardListe boardListe;

    public CreateNewAuftrag(Context context,
                            Auftrag auftragKarte,
                            BoardListe boardListe,
                            CreateNewAuftragListener createNewAuftragListener)
    {
        this.contextWeakReference = new SoftReference<>(context);
        this.createNewAuftragListenerWeakReference = new SoftReference<>(createNewAuftragListener);
        this.auftragKarte = auftragKarte;
        this.boardListe = boardListe;
    }

    public interface CreateNewAuftragListener
    {
        void onResult(Auftrag auftragKarte, boolean success);
    }

    @Override
    public JSONObject bindData() throws JSONException, JsonProcessingException
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
            Context context = contextWeakReference.get();
            if(context != null)
            {
                GlobalBroadCasts.sendBroadcastNewCardCreated(context,
                        auftragKarte, boardListe.getmBoardListeId());
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "failed: " + ec);
        }
        finally
        {
            runUI(new Runnable() {
                @Override
                public void run()
                {
                    CreateNewAuftragListener createNewAuftragListener = createNewAuftragListenerWeakReference.get();
                    if(createNewAuftragListener != null)
                    {
                        createNewAuftragListener.onResult(auftragKarte, success);
                    }
                }
            });
        }
    }

    @Override
    public void onLibraryError(Exception e)
    {
        runUI(new Runnable() {
            @Override
            public void run()
            {
                CreateNewAuftragListener createNewAuftragListener = createNewAuftragListenerWeakReference.get();
                if(createNewAuftragListener != null)
                {
                    createNewAuftragListener.onResult(auftragKarte, false);
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
        return "CNA";
    }

    @Override
    public Session getSession()
    {
        return LoginWorkerSession.SessionHolder.getSession();
    }
}
