package esaph.filing.workers;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import esaph.elib.esaphcommunicationservices.EsaphCommunicationClient;
import esaph.elib.esaphcommunicationservices.EsaphPipe;
import esaph.elib.esaphcommunicationservices.EsaphSocketCenter;
import esaph.elib.esaphcommunicationservices.Session;
import esaph.filing.Board.Model.Board;
import esaph.filing.Board.ShowBoardContent.Model.BoardListe;
import esaph.filing.Utils.LoadingState.LoadingStateHandler;
import esaph.filing.Utils.SocketConfig;

public abstract class LoadMyListFromBoard extends EsaphCommunicationClient
{
    private SoftReference<Context> contextWeakReference;
    private SoftReference<LoadingStateHandler> loadingStateHandlerWeakReference;
    private SoftReference<DataLoadListenerBoardListenGetter> dataLoadListenerBoardListWeakReference;
    private Board board;

    public LoadMyListFromBoard(Context context,
                               LoadingStateHandler loadingStateHandler,
                               Board board,
                               DataLoadListenerBoardListenGetter dataLoadListenerBoardListe)
    {
        this.contextWeakReference = new SoftReference<>(context);
        this.loadingStateHandlerWeakReference = new SoftReference<>(loadingStateHandler);
        this.dataLoadListenerBoardListWeakReference = new SoftReference<>(dataLoadListenerBoardListe);
        this.board = board;
    }

    public interface DataLoadListenerBoardListenGetter
    {
        void onDataLoaded(List<BoardListe> data, boolean success);
    }

    @Override
    public JSONObject bindData() throws JSONException, JsonProcessingException
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("BID", board.getmBoardId());
        jsonObject.put("ST", getCount());
        return jsonObject; //do not need to transmitt any kind of data.
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
        runUI(new Runnable()
        {
            @Override
            public void run()
            {
                LoadingStateHandler loadingStateHandler = loadingStateHandlerWeakReference.get();
                if(loadingStateHandler != null)
                {
                    loadingStateHandler.showLoading();
                }
            }
        });

        final List<BoardListe> list = new ArrayList<>();
        try
        {
            JSONObject jsonObject = new JSONObject(esaphPipe.getBufferedReader().readLine());
            JSONArray jsonArrayData = jsonObject.getJSONArray("DATA");

            for (int counter = 0; counter < jsonArrayData.length(); counter++)
            {
                list.add(new BoardListe().createSelfFromMap(jsonArrayData.get(counter).toString()));
            }
            success = jsonObject.getBoolean("SUCCESS");
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
                    DataLoadListenerBoardListenGetter dataLoadListenerBoardList = dataLoadListenerBoardListWeakReference.get();
                    if(dataLoadListenerBoardList != null)
                    {
                        dataLoadListenerBoardList.onDataLoaded(list, success);
                    }
                }
            });
        }
    }

    @Override
    public String getPipeCommand()
    {
        return "LMLFB";
    }

    @Override
    public Session getSession()
    {
        return LoginWorkerSession.SessionHolder.getSession();
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
    public void onLibraryError(Exception e)
    {
        runUI(new Runnable()
        {
            @Override
            public void run()
            {
                DataLoadListenerBoardListenGetter dataLoadListenerBoardList = dataLoadListenerBoardListWeakReference.get();
                if(dataLoadListenerBoardList != null)
                {
                    dataLoadListenerBoardList.onDataLoaded(new ArrayList<BoardListe>(), false);
                }
            }
        });
    }

    public abstract int getCount();
}
