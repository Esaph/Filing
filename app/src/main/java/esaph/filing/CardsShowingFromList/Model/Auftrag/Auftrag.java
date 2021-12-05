package esaph.filing.CardsShowingFromList.Model.Auftrag;

import java.io.Serializable;

import esaph.elib.esaphcommunicationservices.PipeData;
import esaph.filing.KundenManagement.ContactDTO;

public class Auftrag extends PipeData<Auftrag> implements Serializable
{
    public static final String SERIAZABLE_ID = "esaph.filing.ListeAnzeigen.Model.Auftrag.Subs.Auftrag";
    private long mAuftragsId;
    private String mErstelltID;
    private long mAblaufUhrzeit;
    private int mColorCode;
    private int mPrio;
    private String mBeschreibung;
    private String mAufgabe;
    private ContactDTO contactDTO;
    private ViewType viewType;

    public Auftrag(ViewType viewType,
                   int mAuftragId,
                   String mErstelltID,
                   long mAblaufUhrzeit,
                   int mColorCode,
                   int mPrio,
                   String mBeschreibung,
                   String mAufgabe)
    {
        this.viewType = viewType;
        this.mAuftragsId = mAuftragId;
        this.mErstelltID = mErstelltID;
        this.mAblaufUhrzeit = mAblaufUhrzeit;
        this.mColorCode = mColorCode;
        this.mPrio = mPrio;
        this.mBeschreibung = mBeschreibung;
        this.mAufgabe = mAufgabe;
    }

    public Auftrag() {
    }

    public ViewType getViewType() {
        return viewType;
    }


    public long getmAuftragsId() {
        return mAuftragsId;
    }

    public void setmAuftragsId(long mAuftragsId) {
        this.mAuftragsId = mAuftragsId;
    }

    public String getmErstelltID() {
        return mErstelltID;
    }

    public void setmErstelltID(String mErstelltID) {
        this.mErstelltID = mErstelltID;
    }

    public long getmAblaufUhrzeit() {
        return mAblaufUhrzeit;
    }

    public void setmAblaufUhrzeit(long mAblaufUhrzeit) {
        this.mAblaufUhrzeit = mAblaufUhrzeit;
    }

    public int getmColorCode() {
        return mColorCode;
    }

    public void setmColorCode(int mColorCode) {
        this.mColorCode = mColorCode;
    }

    public int getmPrio() {
        return mPrio;
    }

    public void setmPrio(int mPrio) {
        this.mPrio = mPrio;
    }

    public String getmBeschreibung() {
        return mBeschreibung;
    }

    public void setmBeschreibung(String mBeschreibung) {
        this.mBeschreibung = mBeschreibung;
    }

    public String getmAufgabe() {
        return mAufgabe;
    }

    public void setmAufgabe(String mAufgabe) {
        this.mAufgabe = mAufgabe;
    }

    public ContactDTO getContactDTO() {
        return contactDTO;
    }

    public void setContactDTO(ContactDTO contactDTO) {
        this.contactDTO = contactDTO;
    }

    public void setViewType(ViewType viewType) {
        this.viewType = viewType;
    }
}
