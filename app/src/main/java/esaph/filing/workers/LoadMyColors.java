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
import esaph.filing.FilingColorBinding.ColorBinder;
import esaph.filing.Utils.ListObserver;
import esaph.filing.Utils.LoadingState.LoadingStateHandler;
import esaph.filing.Utils.SocketConfig;

public class LoadMyColors extends EsaphCommunicationClient
{
    private SoftReference<Context> contextWeakReference;
    private SoftReference<LoadingStateHandler> loadingStateHandlerWeakReference;
    private SoftReference<DataLoadListenerColorBinder> dataLoadListenerColorBinderSoftReference;
    private long BID;

    public LoadMyColors(Context context,
                        LoadingStateHandler loadingStateHandler,
                        long BID,
                        DataLoadListenerColorBinder colorBinderSoftReference)
    {
        this.contextWeakReference = new SoftReference<>(context);
        this.loadingStateHandlerWeakReference = new SoftReference<>(loadingStateHandler);
        this.dataLoadListenerColorBinderSoftReference = new SoftReference<>(colorBinderSoftReference);
        this.BID = BID;
    }

    public interface DataLoadListenerColorBinder
    {
        void onDataLoaded(List<ColorBinder> data, boolean success);
    }

    @Override
    public JSONObject bindData() throws JSONException
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("BID", BID);
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

        final List<ColorBinder> list = new ArrayList<>();
        try
        {
            JSONObject jsonObject = new JSONObject(esaphPipe.getBufferedReader().readLine());
            success = jsonObject.getBoolean("SUCCESS");
            JSONArray jsonArrayData = jsonObject.getJSONArray("DATA");

            for (int counter = 0; counter < jsonArrayData.length(); counter++)
            {
                list.add(new ColorBinder().createSelfFromMap(jsonArrayData.get(counter).toString()));
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
                    DataLoadListenerColorBinder dataLoadListenerColorBinder = dataLoadListenerColorBinderSoftReference.get();
                    if(dataLoadListenerColorBinder != null)
                    {
                        dataLoadListenerColorBinder.onDataLoaded(list, success);
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
                DataLoadListenerColorBinder dataLoadListenerColorBinder = dataLoadListenerColorBinderSoftReference.get();
                if(dataLoadListenerColorBinder != null)
                {
                    dataLoadListenerColorBinder.onDataLoaded(new ArrayList<ColorBinder>(), false);
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
        return "LMCB";
    }

    @Override
    public Session getSession()
    {
        return LoginWorkerSession.SessionHolder.getSession();
    }
}
