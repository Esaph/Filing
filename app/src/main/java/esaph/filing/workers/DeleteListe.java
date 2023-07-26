package esaph.filing.workers;

/*
 * Copyright (c) 2023.
 *  Julian Auguscik
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.ref.SoftReference;

import esaph.elib.esaphcommunicationservices.EsaphCommunicationClient;
import esaph.elib.esaphcommunicationservices.EsaphPipe;
import esaph.elib.esaphcommunicationservices.EsaphSocketCenter;
import esaph.elib.esaphcommunicationservices.Session;
import esaph.filing.Account.HPreferences;
import esaph.filing.Board.ShowBoardContent.Model.BoardListe;
import esaph.filing.R;
import esaph.filing.Utils.SocketConfig;

public class DeleteListe extends EsaphCommunicationClient
{
    private SoftReference<Context> contextWeakReference;
    private SoftReference<DeleteListeListener> deleteListeListenerWeakReference;
    private BoardListe boardListe;

    public DeleteListe(Context context,
                       BoardListe boardListe,
                       DeleteListeListener deleteListeListener)
    {
        this.contextWeakReference = new SoftReference<>(context);
        this.deleteListeListenerWeakReference = new SoftReference<>(deleteListeListener);
        this.boardListe = boardListe;
    }

    public interface DeleteListeListener extends Serializable
    {
        void onResult(BoardListe boardListe, boolean success);
    }

    @Override
    public JSONObject bindData() throws JSONException
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("BID", boardListe.getmBoardListeId());
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
            if(success)
            {
                try
                {
                    HPreferences hPreferences = new HPreferences(contextWeakReference.get());
                    if(hPreferences.getWorkingList() != null && hPreferences.getWorkingList().equals(boardListe))
                    {
                        hPreferences.setWorkingList(null);
                    }
                }
                catch (Exception ec)
                {
                    Log.i(getClass().getName(), "Fatales Problem, deleteListe() liste konnte nicht entfernt werden: " + ec);
                }
            }
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
                    DeleteListeListener deleteListeListener = deleteListeListenerWeakReference.get();
                    if(deleteListeListener != null)
                    {
                        deleteListeListener.onResult(boardListe, success);
                    }
                }
            });
        }
    }

    @Override
    public String getPipeCommand()
    {
        return "DL";
    }

    @Override
    public Session getSession()
    {
        return LoginWorkerSession.SessionHolder.getSession();
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
    public void onLibraryError(Exception e)
    {
        runUI(new Runnable() {
            @Override
            public void run() {
                DeleteListeListener deleteListeListener = deleteListeListenerWeakReference.get();
                if(deleteListeListener != null)
                {
                    deleteListeListener.onResult(boardListe, false);
                }
            }
        });
    }
}
