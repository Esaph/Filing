package esaph.filing.Board.Model;

/*
 * Copyright (c) 2023.
 *  Julian Auguscik
 */

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import esaph.elib.esaphcommunicationservices.PipeData;
import esaph.filing.Account.User;
import esaph.filing.Board.ShowBoardContent.Model.BoardListe;
import esaph.filing.FilingColorBinding.ColorBinder;

public class Board extends PipeData<Board> implements Serializable
{
    public static final String extra_Board = "esaph.filing.Board.Model.Board";

    private long mBoardId;
    private String mBoardName;
    private BoardPolicy boardPolicy;
    private List<User> mListMitglieder = new ArrayList<>();
    private List<BoardListe> mBoardListen;
    private List<ColorBinder> colorBinders;

    public Board(int mBoardId, String mBoardName, BoardPolicy boardPolicy)
    {
        this.mBoardId = mBoardId;
        this.mBoardName = mBoardName;
        this.boardPolicy = boardPolicy;
    }

    public Board() {
    }

    public static String getExtra_Board() {
        return extra_Board;
    }

    public List<ColorBinder> getColorBinders() {
        return colorBinders;
    }

    public void setColorBinders(List<ColorBinder> colorBinders) {
        this.colorBinders = colorBinders;
    }

    public long getmBoardId() {
        return mBoardId;
    }

    public void setmBoardId(long mBoardId) {
        this.mBoardId = mBoardId;
    }

    public String getmBoardName() {
        return mBoardName;
    }

    public void setmBoardName(String mBoardName) {
        this.mBoardName = mBoardName;
    }

    public BoardPolicy getBoardPolicy() {
        return boardPolicy;
    }

    public void setBoardPolicy(BoardPolicy boardPolicy) {
        this.boardPolicy = boardPolicy;
    }

    public List<User> getmListMitglieder() {
        return mListMitglieder;
    }

    public void setmListMitglieder(List<User> mListMitglieder) {
        this.mListMitglieder = mListMitglieder;
    }

    public List<BoardListe> getmBoardListen() {
        return mBoardListen;
    }

    public void setmBoardListen(List<BoardListe> mBoardListen) {
        this.mBoardListen = mBoardListen;
    }

    @Override
    public boolean equals(@Nullable Object obj)
    {
        if(obj instanceof Board)
        {
            return ((Board)obj).getmBoardId() == getmBoardId();
        }
        return super.equals(obj);
    }
}