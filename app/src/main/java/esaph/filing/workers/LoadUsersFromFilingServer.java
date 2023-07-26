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
import esaph.filing.Account.User;
import esaph.filing.Utils.LoadingState.LoadingStateHandler;
import esaph.filing.Utils.SocketConfig;

public abstract class LoadUsersFromFilingServer extends EsaphCommunicationClient
{
    private SoftReference<Context> contextWeakReference;
    private SoftReference<LoadingStateHandler> loadingStateHandlerWeakReference;
    private SoftReference<DataLoadListenerFilingServerUsers> dataLoadListenerFilingServerUsersWeakReference;

    public LoadUsersFromFilingServer(Context context,
                                     LoadingStateHandler loadingStateHandler,
                                     DataLoadListenerFilingServerUsers dataLoadListenerFilingServerUsers)
    {
        this.contextWeakReference = new SoftReference<>(context);
        this.loadingStateHandlerWeakReference = new SoftReference<>(loadingStateHandler);
        this.dataLoadListenerFilingServerUsersWeakReference = new SoftReference<>(dataLoadListenerFilingServerUsers);
    }

    public interface DataLoadListenerFilingServerUsers
    {
        void onDataLoaded(List<User> data, boolean success);
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
        final List<User> list = new ArrayList<>();
        try
        {
            JSONObject jsonObject = new JSONObject(esaphPipe.getBufferedReader().readLine());
            JSONArray jsonArrayData = jsonObject.getJSONArray("DATA");

            for (int counter = 0; counter < jsonArrayData.length(); counter++)
            {
                list.add(new User().createSelfFromMap(jsonArrayData.get(counter).toString()));
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
                    DataLoadListenerFilingServerUsers dataLoadListenerFilingServerUsers = dataLoadListenerFilingServerUsersWeakReference.get();
                    if(dataLoadListenerFilingServerUsers != null)
                    {
                        dataLoadListenerFilingServerUsers.onDataLoaded(list, success);
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
                DataLoadListenerFilingServerUsers dataLoadListenerFilingServerUsers = dataLoadListenerFilingServerUsersWeakReference.get();
                if(dataLoadListenerFilingServerUsers != null)
                {
                    dataLoadListenerFilingServerUsers.onDataLoaded(new ArrayList<User>(), success);
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
        return "LFU";
    }

    @Override
    public Session getSession()
    {
        return LoginWorkerSession.SessionHolder.getSession();
    }

    public abstract int getCount();
}
