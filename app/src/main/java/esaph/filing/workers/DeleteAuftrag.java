package esaph.filing.workers;

/*
 * Copyright (c) 2023.
 *  Julian Auguscik
 */

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

public class DeleteAuftrag extends EsaphCommunicationClient
{
    private SoftReference<Context> contextWeakReference;
    private SoftReference<DeleteAuftragListener> deleteAuftragListenerWeakReference;
    private Auftrag auftragKarte;
    private BoardListe boardListe;

    public DeleteAuftrag(Context context,
                         Auftrag auftragKarte,
                         BoardListe boardListe,
                         DeleteAuftragListener deleteAuftragListener)
    {
        this.contextWeakReference = new SoftReference<>(context);
        this.deleteAuftragListenerWeakReference = new SoftReference<>(deleteAuftragListener);
        this.auftragKarte = auftragKarte;
        this.boardListe = boardListe;
    }

    public interface DeleteAuftragListener
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
                    DeleteAuftragListener deleteAuftragListener = deleteAuftragListenerWeakReference.get();
                    if(deleteAuftragListener != null)
                    {
                        deleteAuftragListener.onResult(auftragKarte, success);
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
                DeleteAuftragListener deleteAuftragListener = deleteAuftragListenerWeakReference.get();
                if(deleteAuftragListener != null)
                {
                    deleteAuftragListener.onResult(auftragKarte, false);
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
        return "DA";
    }

    @Override
    public Session getSession()
    {
        return LoginWorkerSession.SessionHolder.getSession();
    }
}
