package esaph.filing.workers;

/*
 * Copyright (c) 2023.
 *  Julian Auguscik
 */

import android.content.Context;
import android.util.Log;

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
import esaph.filing.Utils.LoadingState.LoadingStateHandler;
import esaph.filing.Utils.SocketConfig;

public abstract class LoadMyBoards extends EsaphCommunicationClient
{
    private SoftReference<Context> contextWeakReference;
    private SoftReference<LoadingStateHandler> loadingStateHandlerWeakReference;
    private SoftReference<DataLoadListenerBoardList> dataLoadListenerBoardListWeakReference;

    public LoadMyBoards(Context context,
                        LoadingStateHandler loadingStateHandler,
                        DataLoadListenerBoardList dataLoadListenerBoardListe)
    {
        this.contextWeakReference = new SoftReference<>(context);
        this.loadingStateHandlerWeakReference = new SoftReference<>(loadingStateHandler);
        this.dataLoadListenerBoardListWeakReference = new SoftReference<>(dataLoadListenerBoardListe);
    }

    public interface DataLoadListenerBoardList
    {
        void onDataLoaded(List<Board> data, boolean success);
    }

    @Override
    public JSONObject bindData() throws JSONException
    {
        JSONObject jsonObject = new JSONObject();
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

        final List<Board> list = new ArrayList<>();
        try
        {
            JSONObject jsonObject = new JSONObject(esaphPipe.getBufferedReader().readLine());
            JSONArray jsonArrayData = jsonObject.getJSONArray("DATA");

            for (int counter = 0; counter < jsonArrayData.length(); counter++)
            {
                list.add(new Board().createSelfFromMap(jsonArrayData.get(counter).toString()));
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
                    DataLoadListenerBoardList dataLoadListenerBoardList = dataLoadListenerBoardListWeakReference.get();
                    if(dataLoadListenerBoardList != null)
                    {
                        dataLoadListenerBoardList.onDataLoaded(list, success);
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
                DataLoadListenerBoardList dataLoadListenerBoardList = dataLoadListenerBoardListWeakReference.get();
                if(dataLoadListenerBoardList != null)
                {
                    dataLoadListenerBoardList.onDataLoaded(new ArrayList<Board>(), false);
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
        return "LMB";
    }

    @Override
    public Session getSession()
    {
        return LoginWorkerSession.SessionHolder.getSession();
    }

    public abstract int getCount();
}
