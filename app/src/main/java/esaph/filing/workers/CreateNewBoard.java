package esaph.filing.workers;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.SoftReference;
import java.lang.ref.SoftReference;

import esaph.elib.esaphcommunicationservices.EsaphCommunicationClient;
import esaph.elib.esaphcommunicationservices.EsaphPipe;
import esaph.elib.esaphcommunicationservices.EsaphSocketCenter;
import esaph.elib.esaphcommunicationservices.Session;
import esaph.filing.Board.Model.Board;
import esaph.filing.R;
import esaph.filing.Utils.SocketConfig;

public class CreateNewBoard extends EsaphCommunicationClient
{
    private SoftReference<Context> contextWeakReference;
    private SoftReference<CreateNewBoardListener> createNewBoardListenerWeakReference;
    private Board board;

    public CreateNewBoard(Context context,
                          Board board,
                          CreateNewBoardListener createNewBoardListener)
    {
        this.contextWeakReference = new SoftReference<>(context);
        this.createNewBoardListenerWeakReference = new SoftReference<>(createNewBoardListener);
        this.board = board;
    }

    public interface CreateNewBoardListener
    {
        void onResult(Board board, boolean success);
    }

    @Override
    public JSONObject bindData() throws JSONException, JsonProcessingException
    {
        return board.getObjectMapJson(board);
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

            this.board = new Board().createSelfFromMap(reply.getJSONObject("DATA").toString());
            success = reply.getBoolean("SUCCESS");
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
                    CreateNewBoardListener createNewBoardListener = createNewBoardListenerWeakReference.get();
                    if(createNewBoardListener != null)
                    {
                        createNewBoardListener.onResult(board, success);
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
                CreateNewBoardListener createNewBoardListener = createNewBoardListenerWeakReference.get();
                if(createNewBoardListener != null)
                {
                    createNewBoardListener.onResult(board, false);
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
        return "CNB";
    }

    @Override
    public Session getSession()
    {
        return LoginWorkerSession.SessionHolder.getSession();
    }
}
