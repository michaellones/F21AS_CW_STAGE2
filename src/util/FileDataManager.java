package util;

import exception.*;
import core.*;
import thread.ControlTowerThread;
import thread.FlightThread;

import java.io.*;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class FileDataManager {

	public List<Flight> allFlights() throws IOException {
		List<Flight> flightList = new ArrayList<>();
		String lineValue;
		FileReader reader = new FileReader("src/file/Flights.txt");
		BufferedReader br = new BufferedReader(reader);
		while ((lineValue = br.readLine()) != null) {
			String[] line = lineValue.split("; ");
			Flight flight = new FlightThread();
			flight.setId(line[0]);
			List<Aeroplane> aeroplanes = getAeroplanes();
			Optional<Aeroplane> aeroplaneOptional = aeroplanes.stream()
					.filter(aeroplane -> aeroplane.getPlaneModel() != null)
					.filter(aeroplane -> aeroplane.getPlaneModel().trim().equalsIgnoreCase(line[1]))
					.findFirst();
			if (aeroplaneOptional.isPresent()) {
				Aeroplane plane = aeroplaneOptional.get();
				flight.setAeroplane(plane);
			}
			List<Airport> loadAirports = getAirports();
			Optional<Airport> optionalAirport = loadAirports.stream()
					.filter(airport -> airport.getAirportCode() != null)
					.filter(airport -> airport.getAirportCode().equalsIgnoreCase(line[2]))
					.findFirst();
			if (optionalAirport.isPresent()){
				Airport departure = optionalAirport.get();
				flight.setDeparture(departure);
			}

			Optional<Airport> airportOptional = loadAirports.stream()
					.filter(airport -> airport.getAirportCode() != null)
					.filter(airport -> airport.getAirportCode().equalsIgnoreCase(line[3]))
					.findFirst();
			if (airportOptional.isPresent()){
				Airport destination = airportOptional.get();
				flight.setDestination(destination);
			}
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM:dd:yyyy HH:mm");
			LocalDateTime dateTime = LocalDateTime
					.parse(line[4]+" "+line[5], dateTimeFormatter)
					.atZone(ZoneId.of("CET")).toLocalDateTime();
			flight.setDateTime(dateTime);
			List<String> newAirportList = new ArrayList<>();
			for (int i = 6; i < line.length; i++){
				if (line[i] != null){
					newAirportList.add(line[i]);
				}
			}
			List<Airport> airports1 = loadAirports.stream()
					.filter(airport -> airport.getAirportCode() != null)
					.filter(airport -> newAirportList.contains(airport.getAirportCode()))
					.collect(Collectors.toList());
			FlightPlan plan = new FlightPlan(new LinkedList<>(airports1));
			flight.setFlightPlan(plan);
			flightList.add(flight);
			Thread flightThread = new Thread((FlightThread) flight);
			flightThread.start();
		}
		return flightList;
	}

	public List<Aeroplane> getAeroplanes() throws IOException {
		List<Aeroplane> aeroplaneList = new ArrayList<>();
		String lineValue;
		FileReader reader = new FileReader("src/file/Planes.txt");
		BufferedReader br = new BufferedReader(reader);
		while ((lineValue = br.readLine()) != null) {
			String[] line = lineValue.split("; ");
			Aeroplane plane = new Aeroplane();
			plane.setPlaneSpeed(Double.parseDouble(line[2]));
			plane.setConsumedFuel(Double.parseDouble(line[3]));
			plane.setPlaneModel(line[0]);
			plane.setManufacturer(line[1]);
			aeroplaneList.add(plane);
		}
		return aeroplaneList;
	}

	public List<Airport> getAirports() throws IOException {
		List<Airport> airportList = new ArrayList<>();
		String lineValue;
		FileReader reader = new FileReader("src/file/Airports.txt");
		BufferedReader br = new BufferedReader(reader);
		while ((lineValue = br.readLine()) != null) {
			String[] line = lineValue.split("; ");
			Airport airport = new Airport();
			ControlTower controlTower = new ControlTowerThread(new GPSUtils(line[3], line[2]));
			airport.setControlTower(controlTower);
			airport.setAirportCode(line[0]);
			airport.setAirportName(line[1]);
			airportList.add(airport);
			Thread controlTowerThread = new Thread((ControlTowerThread) controlTower);
			controlTowerThread.start();
		}
		return airportList;
	}

	public List<Airline> getAirlines() throws IOException {
		List<Airline> airlineList = new ArrayList<>();
		String lineValue;
		FileReader reader = new FileReader("src/file/Airlines.txt");
		BufferedReader br = new BufferedReader(reader);
		while ((lineValue = br.readLine()) != null) {
			String[] line = lineValue.split("; ");
			airlineList.add(new Airline(line[1], line[0]));
		}
		return airlineList;
	}

    public void writeToFile(List<Flight> flights) throws Exception {
		File f = new File("src/file/Report.txt");
		if (!f.exists()){
			if (f.createNewFile()){
				changeFileContent(flights, f);
			}else {
				throw new Exception("Could not create report file.");
			}
		}else {
			new PrintWriter(f).close();
			changeFileContent(flights, f);
		}
    }

	private void changeFileContent(List<Flight> listOfFlights, File reportFile) throws IOException {
		FileWriter fw = new FileWriter(reportFile.getPath());
//		System.out.println(reportFile.getPath());
		BufferedWriter bw = new BufferedWriter(fw);
		Map<Airline, List<Flight>> airlineMap = listOfFlights.stream()
				.filter(flight -> flight.getAirline() != null)
				.collect(Collectors.groupingBy(Flight::getAirline));
		for (Airline airline: airlineMap.keySet()){
			List<Flight> flightList = airlineMap.get(airline);
			bw.write(airline.getAirlineName());
			bw.write("\nNumber of flights: "+flightList.size());
			double totalDistance = flightList.stream()
					.mapToDouble(flight -> {
						try {
							return flight.distanceTravelled();
						} catch (DataNotFoundException e) {
							System.out.println(e.getMessage());
						}
						return 0;
					}).sum();
			DecimalFormat decimalFormat = new DecimalFormat("###.##");
			bw.write("\nDistance covered (km): "+decimalFormat.format(totalDistance));

			OptionalDouble averageConsumedFuel = flightList.stream()
					.mapToDouble(flight -> {
						try {
							return flight.consumedFuel();
						} catch (DataNotFoundException e) {
							e.printStackTrace();
						}
						return 0;
					}).average();
			if (averageConsumedFuel.isPresent())
				bw.write("\nAverage Fuel Consumption: "+decimalFormat.format(averageConsumedFuel.getAsDouble()));

			OptionalDouble averageCO2emitted = flightList.stream()
					.mapToDouble(flight -> {
						try {
							return flight.co2Emitted();
						} catch (DataNotFoundException e) {
							System.out.println(e.getMessage());
						}
						return 0;
					}).average();
			if (averageCO2emitted.isPresent())
				bw.write("\nAverage CO2 Emission: "+decimalFormat.format(averageCO2emitted.getAsDouble()));
			bw.write("\n \n");
		}
		bw.close();
	}
}
