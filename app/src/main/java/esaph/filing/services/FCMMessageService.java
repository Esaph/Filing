package esaph.filing.services;

/*
 * Copyright (c) 2023.
 *  Julian Auguscik
 */

import androidx.annotation.NonNull;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Map;
import esaph.filing.workers.SendTokenToServer;

public class FCMMessageService extends FirebaseMessagingService
{
    private static final String CMD_AUFTRAG_CHANGE = "NA";

    @Override
    public void onNewToken(@NonNull String s)
    {
        super.onNewToken(s);
        new SendTokenToServer(getApplicationContext(),
                s,
                new SendTokenToServer.TokenListener()
                {
                    @Override
                    public void onTokenRegistered(boolean success)
                    {
                        // TODO: 22.11.2019 make this what if ?
                    }
                }).transferSynch();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage)
    {
        super.onMessageReceived(remoteMessage);
        Map<String, String> map = remoteMessage.getData();
        String CMD = map.get("CMD");
        if(CMD != null)
        {
            switch (CMD)
            {
                case FCMMessageService.CMD_AUFTRAG_CHANGE:



                    break;
            }
        }
    }
}
