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
import esaph.filing.Board.Model.Board;
import esaph.filing.Board.ShowBoardContent.Model.BoardListe;
import esaph.filing.CardsShowingFromList.ListenAnzeigen.CreateNewListListener;
import esaph.filing.R;
import esaph.filing.Utils.SocketConfig;

public class CreateNewList extends EsaphCommunicationClient
{
    private SoftReference<Context> contextWeakReference;
    private SoftReference<CreateNewListListener> createNewListListenerWeakReference;
    private BoardListe boardListe;
    private Board board;

    public CreateNewList(Context context,
                         Board board,
                         BoardListe boardListe,
                         CreateNewListListener createNewListListener)
    {
        this.contextWeakReference = new SoftReference<>(context);
        this.createNewListListenerWeakReference = new SoftReference<>(createNewListListener);
        this.boardListe = boardListe;
        this.board = board;
    }

    @Override
    public JSONObject bindData() throws JSONException, JsonProcessingException
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("BOARD", board.getObjectMapJson(board));
        jsonObject.put("BLIST", boardListe.getObjectMapJson(boardListe));
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
            boardListe = new BoardListe().createSelfFromMap(reply.getJSONObject("DATA").toString());
            success = reply.getBoolean("SUCCESS");
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "failed: " + ec);
            success = false;
        }
        finally
        {
            runUI(new Runnable()
            {
                @Override
                public void run()
                {
                    CreateNewListListener createNewListListener = createNewListListenerWeakReference.get();
                    if(createNewListListener != null)
                    {
                        createNewListListener.onResult(boardListe, success);
                    }
                }
            });
        }
    }

    @Override
    public void onLibraryError(Exception e) {
        runUI(new Runnable()
        {
            @Override
            public void run()
            {
                CreateNewListListener createNewListListener = createNewListListenerWeakReference.get();
                if(createNewListListener != null)
                {
                    createNewListListener.onResult(boardListe, false);
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
        return "CNL";
    }

    @Override
    public Session getSession()
    {
        return LoginWorkerSession.SessionHolder.getSession();
    }
}
