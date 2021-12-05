package esaph.filing.Account;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import java.util.Arrays;

import esaph.filing.Board.Model.Board;
import esaph.filing.Board.ShowBoardContent.Model.BoardListe;
import esaph.filing.CardsShowingFromList.Sorting.KartenSortMethods;
import esaph.filing.FilingColorBinding.ColorBinder;
import esaph.filing.Utils.EncryptUtils;

public class HPreferences
{
    private static final String PREF_UID = "UID";
    private static final String PREF_VORNAME = "ObjectC";
    private static final String PREF_NACHNAME = "ObjectD";
    private static final String PREF_OAUTH_ID_TOKEN = "Object";
    private static final String PREF_FILE_NAME = "UAD";
    private static final String PREF_HOME_SORTING = "HOME_SORTING";
    private static final String PREF_FILING_SERVER_ADRESS = "FSA";
    private static final String PREF_FILING_TMS_DEST = "TMSD";

    private static final String PREF_OBJECT_WORKING_BOARD = "PREF_OBJECT_WORKING_BOARD";
    private static final String PREF_OBJECT_TOUREN_COLOR_FILTER = "PREF_OBJECT_COLOR_BINDER";
    private static final String PREF_OBJECT_WORKING_LIST = "PREF_OBJECT_WORKING_LIST";

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public HPreferences(Context context)
    {
        preferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.apply();
    }

    public synchronized void logOut()
    {
        editor.remove(PREF_UID);
        editor.remove(PREF_VORNAME);
        editor.remove(PREF_NACHNAME);
        editor.remove(PREF_OAUTH_ID_TOKEN);
        editor.remove(PREF_FILE_NAME);
        editor.remove(PREF_FILING_TMS_DEST);
        editor.remove(PREF_OBJECT_TOUREN_COLOR_FILTER);
        editor.remove(PREF_OBJECT_WORKING_LIST);
        editor.remove(PREF_HOME_SORTING);
        editor.remove(PREF_OBJECT_WORKING_BOARD);
        editor.remove(PREF_OBJECT_WORKING_LIST);
        editor.remove(PREF_FILING_SERVER_ADRESS);
        editor.apply();
    }

    public void setOAuthIdToken(String Password)
    {
        EncryptUtils encryptUtils = new EncryptUtils();
        try
        {
            editor.putString(PREF_OAUTH_ID_TOKEN, Arrays.toString(encryptUtils.encryptMsg(Password, encryptUtils.generateKey(Password))));
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "setOAuthIdToken: " + ec);
        }
    }

    public String getOAuthIdTokenEncrypted(String passwort)
    {
        EncryptUtils encryptUtils = new EncryptUtils();
        try
        {
            String stringArray = this.preferences.getString(PREF_OAUTH_ID_TOKEN, ""); //Lese verschlüsseltes passwort aus datei.

            if (!stringArray.isEmpty())
            {
                String[] split = stringArray.substring(1, stringArray.length()-1).split(", ");
                byte[] array = new byte[split.length];

                for (int i = 0; i < split.length; i++)
                {
                    array[i] = Byte.parseByte(split[i]);
                }

                return encryptUtils.decryptMsg(array, encryptUtils.generateKey(passwort));
            }

            return "";
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getOAuthIdTokenEncrypted() in preferences failed(): " + ec);
            return null;
        }
    }

    public String getUID()
    {
        return this.preferences.getString(PREF_UID, "");
    }

    public String getVorname()
    {
        return this.preferences.getString(PREF_VORNAME, "");
    }

    public String getNachname()
    {
        return this.preferences.getString(PREF_NACHNAME, "");
    }

    public void setHomeSortingMethod(KartenSortMethods kartenSortMethods)
    {
        editor.putString(HPreferences.PREF_HOME_SORTING, kartenSortMethods.name());
        editor.apply();
    }

    public KartenSortMethods getHomeSortingMethod()
    {
        return KartenSortMethods.valueOf(preferences.getString(HPreferences.PREF_HOME_SORTING, KartenSortMethods.BY_PRIORITY.name()));
    }



    public void setWorkingBench(Board board)
    {
        setWorkingList(null);
        Gson gson = new Gson();
        String json = gson.toJson(board);
        editor.putString(HPreferences.PREF_OBJECT_WORKING_BOARD, json);
        editor.apply();
    }

    public Board getWorkingBench()
    {
        Gson gson = new Gson();
        String json = preferences.getString(HPreferences.PREF_OBJECT_WORKING_BOARD, "");
        return gson.fromJson(json, Board.class);
    }

    public ColorBinder getTourenColorFilter()
    {
        Gson gson = new Gson();
        String json = preferences.getString(HPreferences.PREF_OBJECT_TOUREN_COLOR_FILTER, "");
        return gson.fromJson(json, ColorBinder.class);
    }

    public void setTourenColorFilter(ColorBinder colorFilter)
    {
        Gson gson = new Gson();
        String json = gson.toJson(colorFilter);
        editor.putString(HPreferences.PREF_OBJECT_TOUREN_COLOR_FILTER, json);
        editor.commit();
    }


    public void setWorkingList(BoardListe boardListe)
    {
        Gson gson = new Gson();
        String json = gson.toJson(boardListe);
        editor.putString(HPreferences.PREF_OBJECT_WORKING_LIST, json);
        editor.apply(); //Must be apply, do not run on not UI Thread!
    }

    public BoardListe getWorkingList()
    {
        Gson gson = new Gson();
        String json = preferences.getString(HPreferences.PREF_OBJECT_WORKING_LIST, "");
        return gson.fromJson(json, BoardListe.class);
    }

    public void setFilingServerAdress(String filingServer) throws Exception
    {
        EncryptUtils encryptUtils = new EncryptUtils();
        String encrypted = Arrays.toString(encryptUtils.encryptMsg(filingServer, encryptUtils.generateKey("ROOSJDwwOPAIj829")));
        editor.putString(HPreferences.PREF_FILING_SERVER_ADRESS, encrypted);
        editor.apply();
    }

    public String getFilingServerAdress() throws Exception
    {
        EncryptUtils encryptUtils = new EncryptUtils();

        String encryped = "";

        String stringArray = this.preferences.getString(HPreferences.PREF_FILING_SERVER_ADRESS, ""); //Lese verschlüsseltes passwort aus datei.
        if (!stringArray.isEmpty())
        {
            String[] split = stringArray.substring(1, stringArray.length()-1).split(", ");
            byte[] array = new byte[split.length];

            for (int i = 0; i < split.length; i++)
            {
                array[i] = Byte.parseByte(split[i]);
            }

            encryped = encryptUtils.decryptMsg(array, encryptUtils.generateKey("ROOSJDwwOPAIj829"));
        }

        int lastIndex = encryped.lastIndexOf(':');
        if(lastIndex > -1)
        {
            return encryped.substring(0, lastIndex);
        }

        throw new Exception("Filing Serveraddress not specified.");
    }

    public int getFilingServerPort() throws Exception
    {

        EncryptUtils encryptUtils = new EncryptUtils();

        String encryped = "";

        String stringArray = this.preferences.getString(HPreferences.PREF_FILING_SERVER_ADRESS, ""); //Lese verschlüsseltes passwort aus datei.
        if (!stringArray.isEmpty())
        {
            String[] split = stringArray.substring(1, stringArray.length()-1).split(", ");
            byte[] array = new byte[split.length];

            for (int i = 0; i < split.length; i++)
            {
                array[i] = Byte.parseByte(split[i]);
            }

            encryped = encryptUtils.decryptMsg(array, encryptUtils.generateKey("ROOSJDwwOPAIj829"));
        }

        int lastIndex = encryped.lastIndexOf(':');
        if(lastIndex > -1)
        {
            return Integer.parseInt(encryped.substring(encryped.lastIndexOf(':')+1));
        }

        return -1;
    }

    public String getTMSDestination() throws Exception
    {
        return encrypt(preferences.getString(HPreferences.PREF_FILING_TMS_DEST, ""));
    }

    public void setTMSDestination(String address) throws Exception
    {
        editor.putString(HPreferences.PREF_FILING_TMS_DEST, crypto(address));
        editor.commit();
    }


    private String crypto(String which) throws Exception
    {
        EncryptUtils encryptUtils = new EncryptUtils();
        return Arrays.toString(encryptUtils.encryptMsg(which, encryptUtils.generateKey("ROOSJDwwOPAIj829")));
    }

    private String encrypt(String crypto) throws Exception
    {
        if (!crypto.isEmpty())
        {
            EncryptUtils encryptUtils = new EncryptUtils();
            String[] split = crypto.substring(1, crypto.length()-1).split(", ");
            byte[] array = new byte[split.length];

            for (int i = 0; i < split.length; i++)
            {
                array[i] = Byte.parseByte(split[i]);
            }

            return encryptUtils.decryptMsg(array, encryptUtils.generateKey("ROOSJDwwOPAIj829"));
        }
        throw new Exception("Crypto string is empty");
    }

    public class HPreferencesException extends Exception
    {
    }
}
