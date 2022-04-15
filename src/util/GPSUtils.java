package util;

import core.GPSCoordinate;

public class GPSUtils extends GPSCoordinate {

    public GPSUtils(String longitude, String latitude) {
        super(longitude, latitude);
    }

    //Method from the support program given by the teacher available here:
    //https://gitlab-student.macs.hw.ac.uk/f21as_2022/cw2_support/-/blob/master/src/p4_gps/GPS.java
    /**
     * Add a distance d to the current GPS in the direction of the second GPS (g).
     * Calculate the corresponding intermediate GPS position.
     * The method is more precise as it takes into consideration the curvature of the line between this and g.
     * @param g the direction towards which we add d
     * @param d the added distance in km
     * @return
     */
    public GPSUtils addCircleDistance(GPSCoordinate directionCoordinate, double distance) {

        double r_lat1 = this.getLatInRadian();
        double r_lat2 = directionCoordinate.getLatInRadian();
        double r_lon1= this.getLngInRadian();
        double r_lon2 = directionCoordinate.getLngInRadian();

        double angular = distance/6371.0;
        double f = distance/circleDistance(directionCoordinate);
        double a = Math.sin(angular*(1-f))/Math.sin(angular);
        double b = Math.sin(angular * f) / Math.sin(angular);

        //calculate Cartesian coordinates
        double x = a * Math.cos(r_lat1)* Math.cos(r_lon1) + b * Math.cos(r_lat2) * Math.cos(r_lon2);
        double y = a * Math.cos(r_lat1)* Math.sin(r_lon1) + b * Math.cos(r_lat2) * Math.sin(r_lon2);
        double z = a *  Math.sin(r_lat1) + b * Math.sin(r_lat2);

        //calculate lat and lon for intermediate point
        double r_latx = Math.atan2(z,  Math.sqrt(x*x + y*y));
        double r_lonx = Math.atan2(y, x);

        //System.out.println(r_lat1);
        //System.out.println(r_latx);

        return new GPSUtils(this.toLongitude(r_lonx*180/Math.PI), toLatitude(r_latx*180/Math.PI));

    }

    //Method from the support program given by the teacher available here:
    //https://gitlab-student.macs.hw.ac.uk/f21as_2022/cw2_support/-/blob/master/src/p4_gps/GPS.java
    /**
     * Calculate the great circle distance between this GPS and the given GPS position.
     * See illustration: <img src="./doc-files/great_circle.png" />
     * @param g the second GPS position
     * @return
     */
    public double circleDistance(GPSCoordinate directionCoordinate) {
        double phi1 = this.getLatInRadian(); //radian
        double theta1 = this.getLngInRadian(); //radian
        double phi2 = directionCoordinate.getLatInRadian(); //radian
        double theta2 = directionCoordinate.getLngInRadian(); //radian
        //calculate differences
        double dphi = phi2 - phi1;
        double dtheta = theta2 - theta1;
        //calculate the great circle arc distance
        double d1 = Math.pow(Math.sin(dphi/2), 2) + Math.cos(phi1) * Math.cos(phi2) * Math.pow(Math.sin(dtheta/2),2);
        double d2 = 2 * Math.asin(Math.sqrt(d1));
        return 6371.0 * d2;
    }
}
