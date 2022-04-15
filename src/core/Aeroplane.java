package core;
public class Aeroplane {

    private String planeModel;
    private Double consumedFuel;
    private Double planeSpeed;
    private String Manufacturer;

    
    public Aeroplane(String planeModel, Double planeSpeed, String Manufacturer, Double consumedFuel) {
    	setPlaneModel(planeModel);
    	setPlaneSpeed(planeSpeed);
    	setManufacturer(Manufacturer);
    	setConsumedFuel(consumedFuel);
    }

    public Aeroplane() {
    }

    public String getPlaneModel() {
        return planeModel;
    }

    public void setPlaneModel(String planeModel) {
        this.planeModel = planeModel;
    }

    public Double getPlaneSpeed() {
        return planeSpeed;
    }

    public void setPlaneSpeed(Double planeSpeed) {
        this.planeSpeed = planeSpeed;
    }

    public String getManufacturer() {
        return Manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        Manufacturer = manufacturer;
    }

    public Double getConsumedFuel() {
        return consumedFuel;
    }

    public void setConsumedFuel(Double consumedFuel) {
        this.consumedFuel = consumedFuel;
    }
    
    @Override
    public String toString() {
    	return this.getPlaneModel();
    }

    public double getSpeedInMetrePerSecond(){
        return this.planeSpeed * 10/36;
    }

    public Double getFuelConsumedPerKilometer(){
        return consumedFuel/100;
    }

    public Double getFuelConsumedPerMeter(){
        return consumedFuel/100000;
    }

}
