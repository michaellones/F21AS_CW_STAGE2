package core;

import exception.DataNotFoundException;
import util.GPSUtils;

public class ControlTower {
    private GPSUtils gpsCoordinate;

    public ControlTower(GPSUtils gpsCoordinate) {
        this.gpsCoordinate = gpsCoordinate;
    }

    public ControlTower() {
    }

    public GPSUtils getGpsCoordinate() {
        return gpsCoordinate;
    }

    public void setGpsCoordinate(GPSUtils gpsCoordinate){
        this.gpsCoordinate = gpsCoordinate;
    }

    public Double distanceBetween(ControlTower controlTower) throws DataNotFoundException {
        GPSCoordinate controlTowerGpsCoordinate = controlTower.getGpsCoordinate();
        if (controlTowerGpsCoordinate == null){
            throw new DataNotFoundException("GPS coordinates was not found.");
        }

        return this.getGpsCoordinate().circleDistance(controlTowerGpsCoordinate);
    }
    
    public boolean compareTo(ControlTower tower) {
    	
    	if(tower.getGpsCoordinate()
    						.getLatitude()
    						.equals(this.getGpsCoordinate().getLatitude())
    	   &&
    	   tower.getGpsCoordinate()
    	   					.getLongitude()
    	   					.equals(this.getGpsCoordinate().getLongitude())) {
    			
    		return true;
    	}
    	
    	
    	return false;
    }
}
