package core;

import exception.DataNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class Flight {

    private String id;
    private Airport destination;
    private FlightPlan flightPlan;
    private Airline airline;
    private LocalDateTime dateTime;
    private Aeroplane aeroplane;
    private Airport departure;

    public Flight() {
    }

    public Flight(String id,
    			  Aeroplane aeroplane,
    			  Airport departure,
    			  Airport destination,
    			  LocalDateTime dateTime,
    			  FlightPlan plan) {
    	setId(id);
    	setAeroplane(aeroplane);
    	setDeparture(departure);
    	setDestination(destination);
    	setDateTime(dateTime);
    	setFlightPlan(plan);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Aeroplane getAeroplane() {
        return aeroplane;
    }

    public void setAeroplane(Aeroplane aeroplane) {
        this.aeroplane = aeroplane;
    }

    public Airport getDeparture() {
        return departure;
    }

    public void setDeparture(Airport departure) {
        this.departure = departure;
    }

    public Airport getDestination() {
        return destination;
    }

    public void setDestination(Airport destination) {
        this.destination = destination;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public FlightPlan getFlightPlan() {
        return flightPlan;
    }

    public void setFlightPlan(FlightPlan flightPlan) {
        this.flightPlan = flightPlan;
    }

    public Airline getAirline() {
        return airline;
    }

    public void setAirline(Airline airline) {
        this.airline = airline;
    }

    public Double distanceTravelled() throws DataNotFoundException {
        double dist = 0;
        ControlTower tower = this.departure.getControlTower();
        if (tower == null) {
            throw new DataNotFoundException("No control tower for this flight not found.");
        }
        List<ControlTower> controlTowers = flightPlan.getControlTowers();
        if (controlTowers.isEmpty()) {
            throw new DataNotFoundException("Control towers to visit is empty.");
        }
        for (ControlTower otherControlTower : controlTowers) {
            Double distanceBetweenControlTower = tower
                    .distanceBetween(otherControlTower);
            tower = otherControlTower;
            dist += distanceBetweenControlTower;
        }
        return dist;
    }

    public Double timeForFlight() throws DataNotFoundException {
        double time = 0.0;
        Aeroplane plane = this.getAeroplane();
        Double speed = plane.getPlaneSpeed();
        FlightPlan flightPlan = this.getFlightPlan();
        ControlTower departureControlTower = this.departure.getControlTower();
        if (departureControlTower == null){
            throw new DataNotFoundException("Departure airport control tower not found.");
        }
        List<ControlTower> controlTowerList = flightPlan.getControlTowers();
        if (controlTowerList.isEmpty()) {
            throw new DataNotFoundException("No control towers found.");
        }
        time = distanceTravelled() / speed;
        return time;
    }

    public Double consumedFuel() throws DataNotFoundException {
        Aeroplane plane = this.getAeroplane();
        Double fuel = plane.getFuelConsumedPerKilometer();
        if (fuel == null){
            throw new DataNotFoundException("Fuel consumption for this plane is null.");
        }
        Double distance = this.distanceTravelled();
        return distance * fuel;
    }

    public Double co2Emitted() throws DataNotFoundException {
        return this.consumedFuel() * 8.31;
    }
}
