package esaph.filing.TourenPlaner;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class EsaphGoogleTripsRequestBuilder
{
    private List<RoadTrip> roadTrips = new ArrayList<>();
    private String destinitionAddress;
    private EsaphGoogleTripsRequestBuilder(String destinitionAddress)
    {
        this.destinitionAddress = destinitionAddress;
    }

    public String getDestinitionAddress() {
        return destinitionAddress;
    }

    public static EsaphGoogleTripsRequestBuilder roadTrip(String destinitionAddress)
    {
        return new EsaphGoogleTripsRequestBuilder(destinitionAddress);
    }


    public EsaphGoogleTripsRequestBuilder addRoadTrip(RoadTrip trip)
    {
        roadTrips.add(trip);
        return this;
    }

    private String googleTripRequest = "https://www.google.com/maps/dir/?api=1&destination=%s%s&travelmode=driving"; //Destinition, Waypoints

    public Intent build()
    {
        StringBuilder stringBuilderRoad = new StringBuilder();

        for (RoadTrip r :
                roadTrips)
        {
            if(r.getTripAdress() != null && !r.getTripAdress().isEmpty())
            {
                stringBuilderRoad.append("&waypoints=");
                stringBuilderRoad.append(r.tripAdress);
            }
        }

        String roadTripString = String.format(googleTripRequest,
                destinitionAddress,
                stringBuilderRoad.toString());

        Log.i(getClass().getName(), "Roadtrip: " + roadTripString);

        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse(roadTripString));
        intent.setPackage("com.google.android.apps.maps");
        return intent;
    }

    public static class RoadTrip
    {
        private String tripAdress;

        public RoadTrip(String tripAdress)
        {
            this.tripAdress = tripAdress;
        }

        public String getTripAdress() {
            return tripAdress;
        }
    }
}
