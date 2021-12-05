package esaph.filing.Board.Sorting;
import java.util.Comparator;

import esaph.filing.Board.Model.Board;

public class BoardComperator
{
    public static Comparator<? super Board> getComperator()
    {
        return new CompBoardSortByName();
    }
}
