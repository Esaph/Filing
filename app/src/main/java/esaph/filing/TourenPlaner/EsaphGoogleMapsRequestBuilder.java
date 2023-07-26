package esaph.filing.TourenPlaner;

/*
 * Copyright (c) 2023.
 *  Julian Auguscik
 */

import android.content.Intent;
import android.net.Uri;

public class EsaphGoogleMapsRequestBuilder
{
    private String mCountry;
    private String mCity;
    private String mAdress;
    private String mCompleteAdress;

    private EsaphGoogleMapsRequestBuilder()
    {
    }

    public static EsaphGoogleMapsRequestBuilder obtain()
    {
        return new EsaphGoogleMapsRequestBuilder();
    }

    public EsaphGoogleMapsRequestBuilder(String mCompleteAdress) {
        this.mCompleteAdress = mCompleteAdress;
    }

    public static EsaphGoogleMapsRequestBuilder obtain(String mCompleteAdress)
    {
        return new EsaphGoogleMapsRequestBuilder(mCompleteAdress);
    }


    public EsaphGoogleMapsRequestBuilder setmAdress(String mAdress) {
        this.mAdress = mAdress;
        return this;
    }

    public EsaphGoogleMapsRequestBuilder setmCity(String mCity) {
        this.mCity = mCity;
        return this;
    }

    public EsaphGoogleMapsRequestBuilder setmCountry(String mCountry) {
        this.mCountry = mCountry;
        return this;
    }

    public Intent build()
    {
        if(mCompleteAdress != null && !mCompleteAdress.isEmpty())
        {
            String baseModel = String.format("google.navigation:q=%s&mode=d", this.mCompleteAdress);
            return new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse(baseModel));
        }
        else
        {
            String baseModel = String.format("google.navigation:q=%s+%s&mode=d", this.mAdress, this.mCity);
            return new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse(baseModel));
        }
    }
}
