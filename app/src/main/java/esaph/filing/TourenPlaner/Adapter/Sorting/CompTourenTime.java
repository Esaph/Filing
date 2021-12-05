package esaph.filing.TourenPlaner.Adapter.Sorting;

import java.util.Comparator;

import esaph.filing.CardsShowingFromList.Model.Auftrag.Auftrag;

public class CompTourenTime implements Comparator<Auftrag>
{
    @Override
    public int compare(Auftrag o1, Auftrag o2)
    {
        return (int) (o1.getmAblaufUhrzeit() - o2.getmAblaufUhrzeit());
    }
}