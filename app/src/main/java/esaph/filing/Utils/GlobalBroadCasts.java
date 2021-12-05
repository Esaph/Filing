package esaph.filing.Utils;

import android.content.Context;
import android.content.Intent;
import esaph.filing.CardsShowingFromList.Model.Auftrag.Auftrag;

public class GlobalBroadCasts
{
    public static final String action_NewCardCreated = "esaph.filing.createnewauftrag";

    public static final String extraNewCardCreatedAuftragList = "esaph.filing.createnewauftrag.extra.listid";

    public static void sendBroadcastNewCardCreated(Context context, Auftrag auftrag, long LID)
    {
        Intent intent = new Intent();
        intent.putExtra(Auftrag.SERIAZABLE_ID, auftrag);
        intent.putExtra(GlobalBroadCasts.extraNewCardCreatedAuftragList, LID);
        intent.setAction(GlobalBroadCasts.action_NewCardCreated);
        context.sendBroadcast(intent);
    }
}