package esaph.filing.CardsShowingFromList.Sorting;

import java.util.Comparator;
import java.util.Date;

import esaph.filing.CardsShowingFromList.Model.Auftrag.Auftrag;

public class CompSortByDate implements Comparator<Auftrag>
{
    @Override
    public int compare(Auftrag o1, Auftrag o2)
    {
        Date millisFirst = new Date(o1.getmAblaufUhrzeit());
        Date millisSecond = new Date(o2.getmAblaufUhrzeit());

        return millisFirst.compareTo(millisSecond);
    }
}