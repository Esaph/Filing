package esaph.filing.CardsShowingFromList.Sorting;

/*
 * Copyright (c) 2023.
 *  Julian Auguscik
 */

import java.util.Comparator;

import esaph.filing.CardsShowingFromList.Model.Auftrag.Auftrag;

public class CompSortByColor implements Comparator<Auftrag>
{
    @Override
    public int compare(Auftrag o1, Auftrag o2)
    {
        return o1.getmColorCode() - o2.getmColorCode();
    }
}