package esaph.filing.Utils.EsaphTimeCalculations;

/*
 * Copyright (c) 2023.
 *  Julian Auguscik
 */

import android.content.res.Resources;

import java.util.Date;

import esaph.filing.R;

public class EsaphTime
{
    public static String getDateDiff(Resources resources, int idStringPrefix, long date1, long date2)
    {
        StringBuilder stringBuilder = new StringBuilder();
        Date dateTime = new Date(date1);
        Date dateTime2 = new Date(date2);

        long hours = EsaphHours.hoursBetween(dateTime, dateTime2);
        long minutes = EsaphMinutes.minutesBetween(dateTime, dateTime2);
        long days = EsaphDays.daysBetween(dateTime, dateTime2);
        long weeks = EsaphWeeks.weeksBetween(dateTime, dateTime2);

        if(hours <= 0 && minutes <= 0)
        {
            stringBuilder.append(resources.getString(R.string.txt_abgelaufen));
            return stringBuilder.toString();
        }

        stringBuilder.append(resources.getString(idStringPrefix));
        stringBuilder.append(" ");

        if(weeks >= 1)
        {
            stringBuilder.append(resources.getString(R.string.sWeeks, weeks));
        }
        else if(days >= 1)
        {
            stringBuilder.append(resources.getString(R.string.sDays, days));
        }
        else
        {
            if(hours > 0)
            {
                if(minutes > 0)
                {
                    stringBuilder.append(resources.getString(R.string.sHoursSingle, hours));
                }
                else
                {
                    stringBuilder.append(resources.getString(R.string.sHoursWithMinutes, hours, minutes));
                }
            }
            else
            {
                stringBuilder.append(resources.getString(R.string.sMinutes, minutes));
            }
        }
        return stringBuilder.toString();
    }

}
