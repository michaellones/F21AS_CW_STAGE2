package core;
public class Airline {

    private String airlineName;
    private String airlineCode;
    
    public Airline(String airlineName, String airlineCode) {
    	setAirlineName(airlineName);
    	setAirlineCode(airlineCode);
    }

    public String getAirlineName() {
        return airlineName;
    }

    public void setAirlineName(String airlineName) {
        this.airlineName = airlineName;
    }

    public String getAirlineCode() {
        return airlineCode;
    }

    public void setAirlineCode(String airlineCode) {
        this.airlineCode = airlineCode;
    }
    
    @Override
    public String toString() {
    	return this.getAirlineName();
    }

}
