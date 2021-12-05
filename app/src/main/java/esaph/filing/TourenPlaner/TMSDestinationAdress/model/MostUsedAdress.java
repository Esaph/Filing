package esaph.filing.TourenPlaner.TMSDestinationAdress.model;

import org.dizitart.no2.objects.Id;

public class MostUsedAdress
{
    @Id
    private String Adress;

    public MostUsedAdress(String adress) {
        Adress = adress;
    }

    public MostUsedAdress() {
    }

    public String getAdress() {
        return Adress;
    }

    public void setAdress(String adress) {
        Adress = adress;
    }
}
