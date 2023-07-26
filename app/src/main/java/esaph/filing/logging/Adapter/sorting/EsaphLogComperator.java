package esaph.filing.logging.Adapter.sorting;
/*
 * Copyright (c) 2023.
 *  Julian Auguscik
 */

import java.util.Comparator;

import esaph.filing.Board.Model.Board;
import esaph.filing.Board.Sorting.CompBoardSortByName;
import esaph.filing.logging.Adapter.model.EsaphLog;

public class EsaphLogComperator implements Comparator<EsaphLog>
{
    @Override
    public int compare(EsaphLog o1, EsaphLog o2)
    {
        return (int) (o1.getLogTime() - o2.getLogTime());
    }
}