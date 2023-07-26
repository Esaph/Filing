package esaph.filing.Utils.EsaphTimeCalculations;

/*
 * Copyright (c) 2023.
 *  Julian Auguscik
 */

import java.util.Date;

public class EsaphWeeks
{
    public static long weeksBetween(Date date, Date second)
    {
        long diff;
        if(date.getTime() > second.getTime())
        {
            diff = date.getTime() - second.getTime();
        }
        else
        {
            diff = second.getTime() - date.getTime();
        }

        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        return days / 7;
    }
}
