package core;
import util.Utils;

import java.util.regex.Matcher;

public class GPSCoordinate {
    private String latitude;
    private String longitude;


    public GPSCoordinate(String longitude, String latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public GPSCoordinate() {
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public Double convertLngToDegrees(){
        Matcher m = Utils.DMS_LNG_PATTERN.matcher(this.longitude.trim());
        if (m.matches()){
            double lng = convertToDouble(m);
            if ((Math.abs(lng) > 180)) {
                throw new NumberFormatException("Invalid longitude");
            }
            return lng;
        }else {
            throw new NumberFormatException("Malformed DMS coordiniates");
        }
    }

    public Double convertLatToDegrees(){
        Matcher m = Utils.DMS_LAT_PATTERN.matcher(this.latitude.trim());
        if (m.matches()){
            double lat = convertToDouble(m);
            if ((Math.abs(lat) > 180)) {
                throw new NumberFormatException("Invalid latitude");
            }
            return lat;
        }else {
            throw new NumberFormatException("Malformed DMS coordiniates");
        }
    }

    public Double getLatInRadian(){
        return this.convertLatToDegrees() * (Math.PI / 180);
    }

    public Double getLngInRadian(){
        return this.convertLngToDegrees() * (Math.PI / 180);
    }

    private Double convertToDouble(Matcher matcher){
        int i = "".equals(matcher.group(1)) ? 1 : -1;
        double D = Double.parseDouble(matcher.group(2));
        double M = Double.parseDouble(matcher.group(3));
        double S = Double.parseDouble(matcher.group(4));
        int drt = "NE".contains(matcher.group(5)) ? 1 : -1;

        return i * drt * (D + M / 60 + S / 3600 );
    }

    private String convertToDMS(double coordinate) {
        String latlon = "";

        coordinate = Math.abs(coordinate);

        int degree = (int) coordinate;
        double latRem = coordinate - degree;
        int minute = (int) (latRem*60);
        double second = (latRem*60 - minute)*60;

        latlon += degree + "°" + minute + "'" + String.format("%.4f", second).replace(",", ".") + "\"";

        return latlon;
    }

    protected String toLatitude(double latInRad) {
        String lat = convertToDMS(latInRad);

        lat += latInRad > 0 ? "N" : "S";

        return lat;
    }

    protected String toLongitude(double lngInRad) {
        String lon = convertToDMS(lngInRad);

        lon += lngInRad > 0 ? "E" : "W";

        return lon;
    }
}
