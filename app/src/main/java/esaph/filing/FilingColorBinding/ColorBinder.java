package esaph.filing.FilingColorBinding;

/*
 * Copyright (c) 2023.
 *  Julian Auguscik
 */

import android.os.ParcelUuid;

import java.io.Serializable;
import esaph.elib.esaphcommunicationservices.PipeData;

public class ColorBinder extends PipeData<ColorBinder> implements Serializable
{
    public static final String extra = "esaph.filing.FilingColorBinding.colorbinder";

    private String mImportance;
    private int mColor;

    public ColorBinder(String mImportance, int mColor) {
        this.mImportance = mImportance;
        this.mColor = mColor;
    }

    public ColorBinder() {
    }

    public String getmImportance() {
        return mImportance;
    }

    public void setmImportance(String mImportance) {
        this.mImportance = mImportance;
    }

    public int getmColor() {
        return mColor;
    }

    public void setmColor(int mColor) {
        this.mColor = mColor;
    }
}
