package core;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class FlightPlan {

    private LinkedList<Airport> airportList;

    public FlightPlan(LinkedList<Airport> airportLinkedList) {
    	setAirportList(airportLinkedList);
    }

    public List<Airport> getAirportList() {
        return airportList;
    }

    public void setAirportList(LinkedList<Airport> airportList) {
        this.airportList = airportList;
    }

    public List<ControlTower> getControlTowers(){
        return this.getAirportList()
                .stream()
                .map(Airport::getControlTower)
                .collect(Collectors.toList());
    }
}