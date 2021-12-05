package esaph.filing.CardsShowingFromList.ListenAnzeigen;

import esaph.filing.Board.ShowBoardContent.Model.BoardListe;

public interface CreateNewListListener
{
    void onResult(BoardListe boardListe, boolean success);
}