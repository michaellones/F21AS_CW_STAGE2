package core;
public class Airport {

    private String airportName;
    private String airportCode;
    private ControlTower controlTower;

    public Airport() {
    }
    
    public Airport(String airportName, String airportCode, ControlTower controlTower) {
    	setAirportName(airportName);
    	setAirportCode(airportCode);
    	setControlTower(controlTower);
    }

    public String getAirportName() {
        return airportName;
    }

    public void setAirportName(String airportName) {
        this.airportName = airportName;
    }

    public String getAirportCode() {
        return airportCode;
    }

    public void setAirportCode(String airportCode) {
        this.airportCode = airportCode;
    }

    public ControlTower getControlTower() {
        return controlTower;
    }

    public void setControlTower(ControlTower controlTower) {
        this.controlTower = controlTower;
    }
    
    @Override
    public String toString() {
    	return this.getAirportCode();
    }

}
