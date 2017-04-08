package university4credit.universitygear;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.app.AlertDialog;
import android.content.DialogInterface;
import java.util.ArrayList;
import java.util.List;


public class TrackGPS extends Service implements LocationListener {
    private Context mContext;
    private Integer min= 999;

    public TrackGPS(Context a) {
        mContext = a;
    }

    SchoolList getSchoolList() {
        double mindistance = 99999999999.;

        ArrayList<SchoolList> list = new ArrayList<SchoolList>();
        list.add(new SchoolList("Oregon", "Oregon State University", "University of Oregon", "Portland State University", 43.8041, -120.5542));
        list.add(new SchoolList("Alabama", "Alabama State University", "University of Alabama", "Samford University", 32.3182, -86.9023));
        list.add(new SchoolList("Arizona", "Arizona State University", "University of Arizona", "Northern Arizona University", 34.0489, -111.0937));
        list.add(new SchoolList("Arkansas", "Arkansas State University", "University of Arkansas", "University of Central Arkansas", 35.2010, -91.8318));
        list.add(new SchoolList("California", "UC Berkeley", "UC Los Angeles", "Standford University", 36.7783, 119.4179));
        list.add(new SchoolList("Colorado", "Colorado State University", "University of Colorado Boulder", "University of Denver", 39.5501, -105.7821));
        list.add(new SchoolList("Conneticut", "Central Conneticut State University", "University of Conneticut", "Yale University", 41.6032, -73.0877));
        list.add(new SchoolList("Delaware", "Delaware State University", "University of Delaware", "Wilmington University", 38.9108, -75.5277));
        list.add(new SchoolList("DC", "American University", "George Washington University", "Howard University", 38.9072, -77.0369));
        list.add(new SchoolList("Florida", "Florida State University", "University of Miami", "Jacksonville University", 27.6648, -81.5158));
        list.add(new SchoolList("Georgia", "Georgia State University", "University of Georgia", "Kennesaw State University", 32.1656, -82.9001));
        list.add(new SchoolList("Hawaii", "Chaminade University of Honolulu", "University of Hawaii", "Hawaii Pacific University", 19.8968, -155.5828));
        list.add(new SchoolList("Idaho", "Idaho State University", "University of Idaho", "Boise State University", 44.0682, -114.7420));
        list.add(new SchoolList("Illinois", "Chicago State University", "University of Illinois", "UIUC", 40.6331, -89.3985));
        list.add(new SchoolList("Indiana", "Indiana University", "University of Notre Dame", "Purdue University", 40.2672, -86.1349));
        list.add(new SchoolList("Iowa", "Iowa State University", "University of Iowa", "Drake University", 41.8780, -93.0977));
        list.add(new SchoolList("Kansas", "Kansas State University", "University of Kansas", "Wichita State University", 39.0119, -98.4842));
        list.add(new SchoolList("Kentucky", "Murray State University", "University of Kentucky", "Kentucky University", 37.8393, -84.2700));
        list.add(new SchoolList("Louisiana", "Louisiana State University", "University of Louisiana", "Louisiana Tech University", 30.9843, -91.9623));
        list.add(new SchoolList("Maryland", "Morgan State University", "University of Maryland", "Coppin State University", 39.0458, -76.6413));
        list.add(new SchoolList("Masechus", "Boston University", "Harvard University", "Boston College", 42.4072, -71.3824));
        list.add(new SchoolList("Michigan", "Michigan State University", "University of Michigan", "Oakland University", 44.3148, -85.6024));
        list.add(new SchoolList("Minessotta", "Minnesota State University", "University of Minnesota", "Bemidji State University", 46.7296, -94.6859));
        list.add(new SchoolList("Mississippi", "Mississippi State University University", "University of Mississippi", "Mississippi Valley State University", 32.3547, -89.3985));
        list.add(new SchoolList("Missouri", "Missouri State University", "University of Missouri", "St. Louis University", 37.9643, -91.8318));
        list.add(new SchoolList("Montana", "Montana State University", "University of Montana", "Montana State University Billings", 46.8797, -110.3626));
        list.add(new SchoolList("NEbraska", "Creighton State University", "University of Nebraska-Lincoln", "University of Nebraska Omaha", 41.4925, -99.9018));
        list.add(new SchoolList("New Hampshire", "Dartmouth College", "University of New Hampshire", "Franklin Pierce University", 43.1939, -71.5724));
        list.add(new SchoolList("New Jersey", "Princeton University", "St. Peter University", "New Jersey Institute of Technology", 40.0583, -74.4057));
        list.add(new SchoolList("New Mexico", "New Mexico State University", "University of New Mexico", "New Mexico Highland University", 34.5199, -105.8701));
        list.add(new SchoolList("New York", "Columbia University", "University at Buffalo", "Cornell University", 40.7128, -74.0059));
        list.add(new SchoolList("North Carolina", "North Carolina Central University", "University of North Carolina", "High Point University", 35.7596, -79.0193));
        list.add(new SchoolList("North Dakota", "North Dakota State University", "University of North Dakota", "Minot State University", 47.5515, -101.0020));
        list.add(new SchoolList("Ohio", "Ohio State University", "University of Toledo", "Cleveland State University", 40.4173, -82.9071));
        list.add(new SchoolList("Oklahoma", "Oklahoma State University", "University of Oklahoma", "Oral Roberts University", 35.0078, -97.0929));
        list.add(new SchoolList("Pennsylvania", "Lafayette University", "University of Pennsylvania", "St. Joseph University", 41.2033, -77.1945));
        list.add(new SchoolList("Rhode", "Brown University", "University of Rhode Island", "Bryant University", 41.5801, -71.4774));
        list.add(new SchoolList("South CArolina", "South Carolina State University", "University of South Carolina", "Furman University", 33.8361, -81.1637));
        list.add(new SchoolList("South Dakota", "South Dakota State University", "University of South Dakota", "Northern State University", 43.9695, -99.9018));
        list.add(new SchoolList("Tennessee", "Tennessee State University", "University of Tennessee", "Tennessee Technological University", 35.5175, -86.5804));
        list.add(new SchoolList("Texas", "Texas State University", "University of Texas Austin", "Texas Tech University", 31.9686, -99.9018));
        list.add(new SchoolList("Utah", "Utah State University", "University of Utah", "Utah Valley University", 39.3210, -111.0937));
        list.add(new SchoolList("Virginia", "Virginia Tech", "University of Virginia", "University of Richmond", 37.4316, -78.6569));
        list.add(new SchoolList("Washington", "Washington State University", "University of Washington", "Seattle University", 47.7511, -120.7401));
        list.add(new SchoolList("West Virginia", "West Virginia University", "University of Charleston", "Marquee University", 43.8041, -120.5542));
        list.add(new SchoolList("Wisconsin", "University of Wisconsin-Milwaukee", "University of Wisconsin-Green Bay", "University of Wisconsin-Madison", 43.7844, -88.7879));
        list.add(new SchoolList("Default", "Oregon State University", "University of Southern California", "Arizona State University",999, 999));
        Log.e("Check", "Added List");
        LocationManager locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        Log.e("Check", "selfperm");
        if (!(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) && !(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))) {
            Log.e("Check","NotEnabel");
            min = list.size()-1;
        } else {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

                Log.e("Check","asdf");

                return list.get(list.size()-1);
            }
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 10, 6000, this);
            if(locationManager!=null){

                List<String> providers = locationManager.getProviders(true);
                Log.e("Check","Get");
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if(location!=null) {
                        Log.e("Check","Get list");
                        for (int x = 0; x < list.size(); x++) {
                            double temp = list.get(x).CompareLoc(location.getLatitude(), location.getLongitude());
                            Log.e("Temp",String.valueOf(temp));
                            Log.e("MinDist",String.valueOf(mindistance));
                            if (temp < mindistance) {
                                mindistance = temp;
                                min = x;
                                Log.e("x",String.valueOf(x));
                            }
                        }
                        Log.e("Check",list.get(min).State);
                    }
                    else{
                     min = list.size()-1;
                    }
                }


        }


        // Called when a new location is found by the network location provider.


        return list.get(min);
    }
    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
