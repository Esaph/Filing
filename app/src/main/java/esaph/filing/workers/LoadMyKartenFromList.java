package esaph.filing.workers;

/*
 * Copyright (c) 2023.
 *  Julian Auguscik
 */

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
import esaph.filing.CardsShowingFromList.Model.Auftrag.Auftrag;
import esaph.filing.Board.ShowBoardContent.Model.BoardListe;
import esaph.filing.Utils.LoadingState.LoadingStateHandler;
import esaph.filing.Utils.SocketConfig;

public abstract class LoadMyKartenFromList extends EsaphCommunicationClient
{
    private SoftReference<Context> contextWeakReference;
    private SoftReference<LoadingStateHandler> loadingStateHandlerWeakReference;
    private SoftReference<DataLoadListenerKartenFromList> dataLoadListenerBoardListWeakReference;
    private BoardListe boardListe;

    public LoadMyKartenFromList(Context context,
                                LoadingStateHandler loadingStateHandler,
                                BoardListe boardListe,
                                DataLoadListenerKartenFromList dataLoadListenerBoardListe)
    {
        this.contextWeakReference = new SoftReference<>(context);
        this.loadingStateHandlerWeakReference = new SoftReference<>(loadingStateHandler);
        this.dataLoadListenerBoardListWeakReference = new SoftReference<>(dataLoadListenerBoardListe);
        this.boardListe = boardListe;
    }

    public interface DataLoadListenerKartenFromList
    {
        void onDataLoaded(List<Auftrag> data, boolean success);
    }

    @Override
    public JSONObject bindData() throws JSONException, JsonProcessingException
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("LID", boardListe.getmBoardListeId());
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
        final List<Auftrag> list = new ArrayList<>();
        try
        {
            JSONObject jsonObject = new JSONObject(esaphPipe.getBufferedReader().readLine());
            JSONArray jsonArrayData = jsonObject.getJSONArray("DATA");

            for (int counter = 0; counter < jsonArrayData.length(); counter++)
            {
                list.add(new Auftrag().createSelfFromMap(jsonArrayData.get(counter).toString()));
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
                    DataLoadListenerKartenFromList dataLoadListenerBoardList = dataLoadListenerBoardListWeakReference.get();
                    if(dataLoadListenerBoardList != null)
                    {
                        dataLoadListenerBoardList.onDataLoaded(list, success);
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
                DataLoadListenerKartenFromList dataLoadListenerBoardList = dataLoadListenerBoardListWeakReference.get();
                if(dataLoadListenerBoardList != null)
                {
                    dataLoadListenerBoardList.onDataLoaded(new ArrayList<Auftrag>(), false);
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
        return "LA";
    }

    @Override
    public Session getSession()
    {
        return LoginWorkerSession.SessionHolder.getSession();
    }

    public abstract int getCount();
}
