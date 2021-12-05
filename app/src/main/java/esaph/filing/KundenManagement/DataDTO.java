package esaph.filing.KundenManagement;

import java.io.Serializable;

public class DataDTO implements Serializable
{
    private int dataType;

    private String dataValue;

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public String getDataValue() {
        return dataValue;
    }

    public void setDataValue(String dataValue) {
        this.dataValue = dataValue;
    }
}