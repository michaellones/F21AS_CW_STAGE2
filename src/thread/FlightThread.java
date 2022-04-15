package thread;

import core.ControlTower;
import core.Flight;
import core.FlightDto;
import exception.DataNotFoundException;
import util.GPSUtils;

import java.util.ArrayList;
import java.util.List;

public class FlightThread extends Flight implements Runnable {

    private List<ControlTower> watchers = new ArrayList<>();

    private FlightDto flightDto = new FlightDto();

    private long sleepTime = 2000;

    @Override
    public void run() {
        int step = 0;
        ControlTower currentControlTower = getFlightPlan().getAirportList().get(step).getControlTower();
        ControlTower nextControlTower = getFlightPlan().getAirportList().get(step + 1).getControlTower();
        flightDto.setNearestControlTower(currentControlTower);
        flightDto.setCurrentGPSCoordinates(currentControlTower.getGpsCoordinate());
        flightDto.setId(this.getId());
        addToWatcher(flightDto.getNearestControlTower());
        double distanceCovered;
        double timeInHours;
        double distanceBetweenTowers = 0;
        double stepDistance = 0;
        publish();
        while (!flightDto.getHasLanded()){
            sleep(sleepTime);
            timeInHours = getTimeInHours(sleepTime);
            distanceCovered = getAeroplane().getPlaneSpeed() * timeInHours;
            flightDto.setCurrentDistanceFromDeparture(flightDto.getCurrentDistanceFromDeparture() + distanceCovered);
            flightDto.setTimeElapsedInHours(flightDto.getTimeElapsedInHours() + timeInHours);
            double fuelConsumed = flightDto.getCurrentDistanceFromDeparture() * getAeroplane().getFuelConsumedPerKilometer();
            flightDto.setCurrentFuelConsumed(fuelConsumed);
            flightDto.setCO2Emitted(flightDto.getCurrentFuelConsumed() * 8.31);
            try {
                flightDto.setHasLanded(checkStatus(flightDto.getCurrentDistanceFromDeparture()));
                distanceBetweenTowers = currentControlTower.distanceBetween(nextControlTower);
            } catch (DataNotFoundException e) {
                e.printStackTrace();
            }
            GPSUtils gpsCoordinate = flightDto.getCurrentGPSCoordinates()
                    .addCircleDistance(nextControlTower.getGpsCoordinate(), distanceCovered);
            flightDto.setCurrentGPSCoordinates(gpsCoordinate);
            if (!flightDto.getHasLanded() && flightDto.getCurrentDistanceFromDeparture() >= (distanceBetweenTowers + stepDistance)){
                step++;
                currentControlTower = nextControlTower;
                nextControlTower = getFlightPlan().getAirportList().get(step+1).getControlTower();
                stepDistance += distanceBetweenTowers;
            }

            publish();
//            System.out.println(flightDto.getCurrentDistanceFromDeparture() - stepDistance > distanceBetweenTowers/2);
            if (!flightDto.getNearestControlTower().equals(nextControlTower)){
                ControlTower watcher = flightDto.getNearestControlTower();
                flightDto.setNearestControlTower(this.getFlightPlan().getAirportList().get(step+1).getControlTower());
                addToWatcher(flightDto.getNearestControlTower());
                publish();
                watchers.remove(watcher);
            }
        }
    }

    private void addToWatcher(ControlTower nearestControlTower) {
        watchers.add(nearestControlTower);
    }

    private boolean checkStatus(Double distanceCovered) throws DataNotFoundException {
        return distanceCovered >= distanceTravelled();
    }

    private Double getTimeInHours(long sleepTime) {
        return (double)sleepTime/3600000;
    }

    private void sleep(long time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void publish() {
        for(ControlTower watcher: watchers) {
            ((ControlTowerThread) watcher).getFlightDto(flightDto);
        }
    }
}
