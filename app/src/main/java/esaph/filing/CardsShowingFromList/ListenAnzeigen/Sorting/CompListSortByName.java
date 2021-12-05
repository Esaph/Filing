package esaph.filing.CardsShowingFromList.ListenAnzeigen.Sorting;

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