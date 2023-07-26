package esaph.filing.CardsShowingFromList.ListenAnzeigen;

/*
 * Copyright (c) 2023.
 *  Julian Auguscik
 */

import esaph.filing.Board.ShowBoardContent.Model.BoardListe;

public interface CreateNewListListener
{
    void onResult(BoardListe boardListe, boolean success);
}