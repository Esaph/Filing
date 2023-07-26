package esaph.filing.CardsShowingFromList.Sorting;

/*
 * Copyright (c) 2023.
 *  Julian Auguscik
 */

import java.io.Serializable;
import java.util.Comparator;

import esaph.filing.CardsShowingFromList.Model.Auftrag.Auftrag;

public class KartenComperator implements Serializable
{
    public static final String extra_KartenSortMethods = "esaph.filing.KartenAnzeigenListe.Sorting.BoardComperator.KartenSortMethods";

    public static Comparator<? super Auftrag> getComperator(KartenSortMethods fragmentKartenAnzeigenSortMethods)
    {
        switch (fragmentKartenAnzeigenSortMethods)
        {
            case BY_TIME:
                return new CompSortByDate();

            case BY_COLOR:
                return new CompSortByColor();

            case BY_PRIORITY:
                return new CompSortByPriority();

                default:
                    return new CompSortByDate();
        }
    }
}
