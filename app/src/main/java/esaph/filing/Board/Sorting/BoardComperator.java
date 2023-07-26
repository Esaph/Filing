package esaph.filing.Board.Sorting;
/*
 * Copyright (c) 2023.
 *  Julian Auguscik
 */

import java.util.Comparator;

import esaph.filing.Board.Model.Board;

public class BoardComperator
{
    public static Comparator<? super Board> getComperator()
    {
        return new CompBoardSortByName();
    }
}
