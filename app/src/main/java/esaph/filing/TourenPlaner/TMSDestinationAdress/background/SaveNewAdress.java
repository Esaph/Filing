package esaph.filing.TourenPlaner.TMSDestinationAdress.background;

/*
 * Copyright (c) 2023.
 *  Julian Auguscik
 */

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.dizitart.no2.Nitrite;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import esaph.filing.TourenPlaner.TMSDestinationAdress.model.MostUsedAdress;

public class SaveNewAdress extends AsyncTask<Void, Void, Integer>
{
    private SoftReference<Context> contextSoftReference;
    private MostUsedAdress mostUsedAdress;
    private SoftReference<SaveNewAdressDoneListener> saveNewAdressDoneListenerSoftReference;

    public SaveNewAdress(Context context,
                         MostUsedAdress mostUsedAdress,
                         SaveNewAdressDoneListener saveNewAdressDoneListener)
    {
        this.mostUsedAdress = mostUsedAdress;
        this.saveNewAdressDoneListenerSoftReference = new SoftReference<SaveNewAdressDoneListener>(saveNewAdressDoneListener);
        this.contextSoftReference = new SoftReference<>(context);
    }

    public interface SaveNewAdressDoneListener
    {
        void onAdressSaved(boolean success);
    }

    @Override
    protected Integer doInBackground(Void... voids)
    {
        Nitrite nitrite = null;
        int result = 0;
        try
        {
            nitrite = Nitrite.builder().filePath(new File(contextSoftReference.get().getFilesDir(),"nitrite").getAbsolutePath())
                    .openOrCreate("23f23r0ij","wfhawoofo");
            result = nitrite.getRepository(MostUsedAdress.class).insert(mostUsedAdress).getAffectedCount();
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "SaveNewAdress() failed: " + ec);
        }
        finally
        {
            if(nitrite != null)
            {
                nitrite.commit();
                nitrite.close();
            }
        }

        return result;
    }

    @Override
    protected void onPostExecute(Integer integer)
    {
        super.onPostExecute(integer);
        SaveNewAdressDoneListener saveNewAdressDoneListener = saveNewAdressDoneListenerSoftReference.get();
        if(saveNewAdressDoneListener != null)
        {
            saveNewAdressDoneListener.onAdressSaved(integer > 0);
        }
    }
}
