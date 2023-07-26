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

import esaph.filing.Account.HPreferences;
import esaph.filing.TourenPlaner.TMSDestinationAdress.model.MostUsedAdress;

public class RemoveAdress extends AsyncTask<Void, Void, Integer>
{
    private SoftReference<Context> contextSoftReference;
    private MostUsedAdress mostUsedAdress;
    private SoftReference<RemoveAdressDoneListener> saveNewAdressDoneListenerSoftReference;

    public RemoveAdress(Context context,
                        MostUsedAdress mostUsedAdress,
                        RemoveAdressDoneListener saveNewAdressDoneListener)
    {
        this.mostUsedAdress = mostUsedAdress;
        this.saveNewAdressDoneListenerSoftReference = new SoftReference<RemoveAdressDoneListener>(saveNewAdressDoneListener);
        this.contextSoftReference = new SoftReference<>(context);
    }

    public interface RemoveAdressDoneListener
    {
        void onAdressRemoved(boolean success);
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
            result = nitrite.getRepository(MostUsedAdress.class).remove(mostUsedAdress).getAffectedCount();
            HPreferences preferences = new HPreferences(contextSoftReference.get());
            if(result > 0 && preferences.getTMSDestination() != null && preferences.getTMSDestination().equals(mostUsedAdress.getAdress()))
            {
                preferences.setTMSDestination("");
            }
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
        RemoveAdressDoneListener saveNewAdressDoneListener = saveNewAdressDoneListenerSoftReference.get();
        if(saveNewAdressDoneListener != null)
        {
            saveNewAdressDoneListener.onAdressRemoved(integer > 0);
        }
    }
}
