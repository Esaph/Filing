package esaph.filing.logging.Adapter.model;

/*
 * Copyright (c) 2023.
 *  Julian Auguscik
 */

import java.io.Serializable;

import esaph.elib.esaphcommunicationservices.PipeData;

public class EsaphLog extends PipeData<EsaphLog> implements Serializable
{
    private long mBoardId;
    private String Username;
    private String log;
    private long logTime;

    public EsaphLog()
    {
    }

    public EsaphLog(long mBoardId, String username, String log, long logTime) {
        this.mBoardId = mBoardId;
        Username = username;
        this.log = log;
        this.logTime = logTime;
    }

    public long getmBoardId() {
        return mBoardId;
    }

    public void setmBoardId(long mBoardId) {
        this.mBoardId = mBoardId;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public long getLogTime() {
        return logTime;
    }

    public void setLogTime(long logTime) {
        this.logTime = logTime;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }
}