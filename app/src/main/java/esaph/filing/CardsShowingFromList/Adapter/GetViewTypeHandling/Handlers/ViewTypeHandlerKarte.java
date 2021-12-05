package esaph.filing.CardsShowingFromList.Adapter.GetViewTypeHandling.Handlers;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;

import esaph.filing.CardsShowingFromList.Adapter.AdapterKartenAnzeigen;
import esaph.filing.CardsShowingFromList.Model.Auftrag.Auftrag;
import esaph.filing.R;

public class ViewTypeHandlerKarte
{
    public void handleViewType(Context context, Auftrag auftragKarte, AdapterKartenAnzeigen.ViewHolderKarte viewHolderKarte)
    {
        viewHolderKarte.textViewAufgabe.setText(auftragKarte.getmAufgabe());
        /*
        viewHolderKarte.textViewAblauf.setText(

                auftragKarte.getmAblaufUhrzeit() > 0 ?
                        EsaphTime.getDateDiff(context.getResources(),
                                R.string.prefixLauftAb,
                                System.currentTimeMillis(),
                                auftragKarte.getmAblaufUhrzeit())
                        :
                        context.getResources().getString(R.string.nichtFestgelegt));*/

        viewHolderKarte.viewColorLine.setBackgroundColor(auftragKarte.getmColorCode());
        viewHolderKarte.buttonDone.setBackgroundColor(auftragKarte.getmColorCode());


        viewHolderKarte.buttonDone.setBackgroundResource(R.drawable.background_card_color);

        Drawable background = viewHolderKarte.buttonDone.getBackground().mutate(); //Should use mutate, to prevent sharing the drawablestate!
        if (background instanceof ShapeDrawable) {
            ((ShapeDrawable)background).getPaint().setColor(auftragKarte.getmColorCode());
        } else if (background instanceof GradientDrawable) {
            ((GradientDrawable)background).setColor(auftragKarte.getmColorCode());
        } else if (background instanceof ColorDrawable) {
            ((ColorDrawable)background).setColor(auftragKarte.getmColorCode());
        }
    }
}
