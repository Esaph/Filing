package esaph.filing.CardsShowingFromList.ListenAnzeigen.Sorting;

/*
 * Copyright (c) 2023.
 *  Julian Auguscik
 */

import java.util.Comparator;

import esaph.filing.Board.ShowBoardContent.Model.BoardListe;

public class CompListSortByName implements Comparator<BoardListe>
{
    @Override
    public int compare(BoardListe o1, BoardListe o2)
    {
        return o1.getmListenName().compareToIgnoreCase(o2.getmListenName());
    }
}