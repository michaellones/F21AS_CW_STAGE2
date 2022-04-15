package core;

import util.GPSUtils;

public class FlightDto {

    private String id;
    private Airport destination;
    private FlightPlan flightPlan;
    private boolean hasLanded;
    private ControlTower nearestControlTower;
    private double currentDistanceFromDeparture;
    private double currentFuelConsumed;
    private double timeElapsedInHours;
    private double CO2Emitted;
    private GPSUtils currentGPSCoordinates;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Airport getDestination() {
        return destination;
    }

    public void setDestination(Airport destination) {
        this.destination = destination;
    }

    public FlightPlan getFlightPlan() {
        return flightPlan;
    }

    public void setFlightPlan(FlightPlan flightPlan) {
        this.flightPlan = flightPlan;
    }

    public boolean getHasLanded() {
        return hasLanded;
    }

    public void setHasLanded(boolean hasLanded) {
        this.hasLanded = hasLanded;
    }

    public ControlTower getNearestControlTower() {
        return nearestControlTower;
    }

    public void setNearestControlTower(ControlTower nearestControlTower) {
        this.nearestControlTower = nearestControlTower;
    }

    public double getCurrentDistanceFromDeparture() {
        return currentDistanceFromDeparture;
    }

    public void setCurrentDistanceFromDeparture(double currentDistanceFromDeparture) {
        this.currentDistanceFromDeparture = currentDistanceFromDeparture;
    }

    public double getCurrentFuelConsumed() {
        return currentFuelConsumed;
    }

    public void setCurrentFuelConsumed(double currentFuelConsumed) {
        this.currentFuelConsumed = currentFuelConsumed;
    }

    public double getTimeElapsedInHours() {
        return timeElapsedInHours;
    }

    public void setTimeElapsedInHours(double timeElapsedInHours) {
        this.timeElapsedInHours = timeElapsedInHours;
    }

    public double getCO2Emitted() {
        return CO2Emitted;
    }

    public void setCO2Emitted(Double CO2Emitted) {
        this.CO2Emitted = CO2Emitted;
    }

    public GPSUtils getCurrentGPSCoordinates() {
        return currentGPSCoordinates;
    }

    public void setCurrentGPSCoordinates(GPSUtils currentGPSCoordinates) {
        this.currentGPSCoordinates = currentGPSCoordinates;
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof ControlTower)) return false;

        if(((ControlTower) o).getGpsCoordinate().equals(getCurrentGPSCoordinates())) {
            return true;
        }

        return false;
    }
}
