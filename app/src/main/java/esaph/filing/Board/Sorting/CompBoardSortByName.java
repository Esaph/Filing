package esaph.filing.Board.Sorting;

/*
 * Copyright (c) 2023.
 *  Julian Auguscik
 */

import java.util.Comparator;

import esaph.filing.Board.Model.Board;

public class CompBoardSortByName implements Comparator<Board>
{
    @Override
    public int compare(Board o1, Board o2)
    {
        return o1.getmBoardName().compareToIgnoreCase(o2.getmBoardName());
    }
}