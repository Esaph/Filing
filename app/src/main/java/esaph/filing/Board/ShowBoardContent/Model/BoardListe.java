package esaph.filing.Board.ShowBoardContent.Model;

/*
 * Copyright (c) 2023.
 *  Julian Auguscik
 */

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.List;

import esaph.elib.esaphcommunicationservices.PipeData;
import esaph.filing.CardsShowingFromList.Model.Auftrag.Auftrag;

public class BoardListe extends PipeData<BoardListe> implements Serializable
{
    public static final String extra_BoardListe = "esaph.filing.Board.ShowBoardContent.Model.BoardListe";

    private long mBoardListeId;
    private int mPriority;
    private String mListenName;
    private List<Auftrag> auftragKartenListe;

    public BoardListe(long mBoardListeId, String mListenName, int mPriority)
    {
        this.mBoardListeId = mBoardListeId;
        this.mListenName = mListenName;
        this.mPriority = mPriority;
    }

    public BoardListe() {
    }

    public long getmBoardListeId() {
        return mBoardListeId;
    }

    public void setmBoardListeId(long mBoardListeId) {
        this.mBoardListeId = mBoardListeId;
    }

    public int getmPriority() {
        return mPriority;
    }

    public void setmPriority(int mPriority) {
        this.mPriority = mPriority;
    }

    public String getmListenName() {
        return mListenName;
    }

    public void setmListenName(String mListenName) {
        this.mListenName = mListenName;
    }

    public List<Auftrag> getAuftragKartenListe() {
        return auftragKartenListe;
    }

    public void setAuftragKartenListe(List<Auftrag> auftragKartenListe) {
        this.auftragKartenListe = auftragKartenListe;
    }

    @Override
    public boolean equals(@Nullable Object obj)
    {
        if(obj instanceof BoardListe)
        {
            return ((BoardListe)obj).getmBoardListeId() == getmBoardListeId();
        }
        else if(obj instanceof Long)
        {
            return ((Long)obj) == getmBoardListeId();
        }
        return super.equals(obj);
    }
}
