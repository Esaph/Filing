package esaph.filing.Utils;

/*
 * Copyright (c) 2023.
 *  Julian Auguscik
 */

import android.content.Context;

import esaph.elib.esaphcommunicationservices.EsaphSocketCenter;
import esaph.filing.Account.HPreferences;
import esaph.filing.R;

public class SocketConfig
{
    private static EsaphSocketCenter.EsaphSocketCenterConfiguration esaphSocketCenterConfiguration = null;

    public static EsaphSocketCenter.EsaphSocketCenterConfiguration defaultConfig(Context context) throws Exception
    {
        if(esaphSocketCenterConfiguration == null)
        {
            synchronized (EsaphSocketCenter.EsaphSocketCenterConfiguration.class)
            {
                if(esaphSocketCenterConfiguration == null)
                {
                    HPreferences hPreferences = new HPreferences(context);
                    esaphSocketCenterConfiguration = new EsaphSocketCenter.EsaphSocketCenterConfiguration(
                            context,
                            hPreferences.getFilingServerAdress(),
                            hPreferences.getFilingServerPort(),
                            R.raw.client,
                            "g!lonov37",
                            R.raw.clienttruststore,
                            "gev@xom51");
                }
            }
        }
        return esaphSocketCenterConfiguration;
    }
}
