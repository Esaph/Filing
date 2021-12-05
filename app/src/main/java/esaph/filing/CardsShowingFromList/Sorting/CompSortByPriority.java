package esaph.filing.CardsShowingFromList.Sorting;

import java.util.Comparator;

import esaph.filing.CardsShowingFromList.Model.Auftrag.Auftrag;

public class CompSortByPriority implements Comparator<Auftrag>
{
    @Override
    public int compare(Auftrag o1, Auftrag o2)
    {
        return o1.getmPrio() - o2.getmPrio();
    }
}