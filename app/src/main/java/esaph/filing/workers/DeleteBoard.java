package esaph.filing.workers;

/*
 * Copyright (c) 2023.
 *  Julian Auguscik
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.core.io.JsonEOFException;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.SoftReference;

import esaph.elib.esaphcommunicationservices.EsaphCommunicationClient;
import esaph.elib.esaphcommunicationservices.EsaphPipe;
import esaph.elib.esaphcommunicationservices.EsaphSocketCenter;
import esaph.elib.esaphcommunicationservices.Session;
import esaph.filing.Account.HPreferences;
import esaph.filing.Board.Model.Board;
import esaph.filing.R;
import esaph.filing.Utils.SocketConfig;

public class DeleteBoard extends EsaphCommunicationClient
{
    private SoftReference<Context> contextWeakReference;
    private SoftReference<DeleteBoardListener> deleteBoardListenerWeakReference;
    private Board board;

    public DeleteBoard(Context context,
                       Board board,
                       DeleteBoardListener deleteBoardListener)
    {
        this.contextWeakReference = new SoftReference<>(context);
        this.deleteBoardListenerWeakReference = new SoftReference<>(deleteBoardListener);
        this.board = board;
    }

    public interface DeleteBoardListener
    {
        void onResult(Board board, boolean success);
    }

    @Override
    public JSONObject bindData() throws JSONException
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("BID", board.getmBoardId());
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
                HPreferences hPreferences = new HPreferences(contextWeakReference.get());
                if(board.equals(hPreferences.getWorkingBench()))
                {
                    Log.i(getClass().getName(), "DeleteBord has removed current board: " + board.getmBoardName());
                    hPreferences.setWorkingBench(null);
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
                    DeleteBoardListener deleteListeListener = deleteBoardListenerWeakReference.get();
                    if(deleteListeListener != null)
                    {
                        deleteListeListener.onResult(board, success);
                    }
                }
            });
        }
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
        runUI(new Runnable()
        {
            @Override
            public void run()
            {
                DeleteBoardListener deleteListeListener = deleteBoardListenerWeakReference.get();
                if(deleteListeListener != null)
                {
                    deleteListeListener.onResult(board, false);
                }
            }
        });
    }

    @Override
    public String getPipeCommand()
    {
        return "DB";
    }

    @Override
    public Session getSession()
    {
        return LoginWorkerSession.SessionHolder.getSession();
    }
}
