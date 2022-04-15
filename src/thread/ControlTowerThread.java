package thread;

import UI.App;
import core.ControlTower;
import core.FlightDto;
import util.GPSUtils;

import java.util.ArrayList;
import java.util.List;

public class ControlTowerThread extends ControlTower implements Runnable {

    private final ArrayList<FlightDto> flightDtos = new ArrayList<>();

    private List<App> appList = new ArrayList<>();

    private static int sleepTime = 3000;


    public ControlTowerThread(GPSUtils gpsCoordinate) {
        super(gpsCoordinate);
    }

    @Override
    public void run() {
        while(true) {
            sleep(sleepTime);

            synchronized(flightDtos) {
                for(App app: appList) {
                    System.out.println("==="+flightDtos.size());
                    app.send(flightDtos);
                }
            }
        }
    }

    private void sleep(int sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    public void callApp(App observer) {
        this.appList.add(observer);
    }

    public void getFlightDto(FlightDto flightDto) {
        synchronized(this.flightDtos) {
            if(this.equals(flightDto.getNearestControlTower())){
                this.flightDtos.add(flightDto);
            }else {
//                this.flightDtos.remove(flightDto);
            }

        }
    }
}
