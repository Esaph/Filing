package esaph.filing.TourenPlaner.TMSDestinationAdress.background;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.bumptech.glide.load.model.ModelLoader;

import org.dizitart.no2.Nitrite;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import esaph.filing.TourenPlaner.TMSDestinationAdress.model.MostUsedAdress;

public class LoadLatestAdresses extends AsyncTask<Void, Void, List<MostUsedAdress>>
{
    private SoftReference<Context> contextSoftReference;
    private SoftReference<LoadLatestAdressesListener> loadLatestAdressesListenerSoftReference;

    public LoadLatestAdresses(Context context, LoadLatestAdressesListener loadLatestAdressesListener)
    {
        this.contextSoftReference = new SoftReference<>(context);
        this.loadLatestAdressesListenerSoftReference = new SoftReference<>(loadLatestAdressesListener);
    }

    public interface LoadLatestAdressesListener
    {
        void onLoaded(List<MostUsedAdress> list);
    }

    @Override
    protected List<MostUsedAdress> doInBackground(Void... voids)
    {
        Nitrite nitrite = null;
        List<MostUsedAdress> list = new ArrayList<>();
        try
        {
            nitrite = Nitrite.builder()
                    .filePath(new File(contextSoftReference.get().getFilesDir(),"nitrite").getAbsolutePath())
                    .openOrCreate("23f23r0ij","wfhawoofo");

            Iterator<MostUsedAdress> iterator = nitrite.getRepository(MostUsedAdress.class).find().iterator();

            while(iterator.hasNext())
            {
                list.add(iterator.next());
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "LoadLatestAdresses() failed: " + ec);
        }
        finally
        {
            if(nitrite != null)
            {
                nitrite.commit();
                nitrite.close();
            }
        }

        return list;
    }

    @Override
    protected void onPostExecute(List<MostUsedAdress> mostUsedAdresses)
    {
        super.onPostExecute(mostUsedAdresses);
        LoadLatestAdressesListener loadLatestAdressesListener = loadLatestAdressesListenerSoftReference.get();
        if(loadLatestAdressesListener != null)
        {
            loadLatestAdressesListener.onLoaded(mostUsedAdresses);
        }
    }
}
