package university4credit.universitygear;

/**
 * Created by bryan_000 on 3/1/2017.
 */

public class SchoolList {
    String State;
    double latitude ,longitude;
    String Schools[] = new String[3];

    SchoolList(String state, String school1,String school2,String school3, double lat, double lon){
        State = state;
        Schools[0] = school1;
        Schools[1] = school2;
        Schools[2] = school3;
        latitude = lat;
        longitude = lon;
    }
    double CompareLoc(double lan, double lon){
        double diff = Math.sqrt((Math.pow((lon-longitude),2)+Math.pow((lan-latitude),2)));
        return diff;
    }
}
