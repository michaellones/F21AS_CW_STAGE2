package UI;

import com.toedter.calendar.JDateChooserCellEditor;
import exception.*;
import core.*;
import thread.ControlTowerThread;
import util.FileDataManager;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class App extends JFrame {

    private JPanel tablePanel;
    private JTable flightPlanTable;
    private List<Flight> flightList;
    private FileDataManager fileDataManager;
    private int frameWidth;
    private int frameHeight;
    private JButton addFlightButton;
    private JScrollPane tableScrollPane;
    private String[][] flightInfo;
    private JTable flightTable;
    private String globalSelectedFlightCode = "";
    private Map<String, FlightDto> flightDtoHashMap = new HashMap<>();
    private JTextArea distanceTextArea;
    private JTextArea timeTextArea;
    private JTextArea fuelConsumptionTextArea;
    private JTextArea CO2EmissionTextArea;

    private App() throws IOException, DataNotFoundException {
        super("Flight Scheduler");
        fileDataManager = new FileDataManager();
        flightList = new ArrayList<>(fileDataManager.allFlights());
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frameWidth = screenSize.width;
        frameHeight = screenSize.height;
        setSize(new Dimension(frameWidth, frameHeight));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel framePanel = new JPanel();

        flightInfo = getAllFLightData(fileDataManager);
        List<Airport> airports = fileDataManager.getAirports();
        for (Airport airport : airports) {
            ((ControlTowerThread) airport.getControlTower()).callApp(this);
        }

        DefaultTableModel flightTableModel = new DefaultTableModel();
        flightTableModel.setDataVector(flightInfo, flightTableHeader());

        flightTable = new JTable(flightTableModel);
        flightTable.setRowHeight(25);

        tableScrollPane = new JScrollPane(flightTable);

        tableScrollPane.setPreferredSize(new Dimension((frameWidth * 69) / 100, (frameHeight * 45) / 100));
        flightTable.setFillsViewportHeight(true);

        tablePanel = new JPanel();
        tablePanel.add(tableScrollPane);
        tablePanel.setPreferredSize(new Dimension((frameWidth * 70) / 100, frameHeight / 2));
        tablePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(),
                "Flights", TitledBorder.CENTER, TitledBorder.TOP,
                new Font("times new roman", Font.PLAIN, 22)));
        framePanel.add(tablePanel);

        populateFlightTable(flightInfo[0][0], framePanel);

        JLabel distanceJLabel = new JLabel("Distance in KM :");
        JLabel timeJLabel = new JLabel("Time in hr:");
        JLabel fuelConsumptionJLabel = new JLabel("Consumed Fuel in litre:");
        JLabel co2EmittedJLabel = new JLabel("CO2 in kg:");

        distanceTextArea = new JTextArea(1, 5);
        distanceTextArea.setFont(new Font("times new roman", Font.PLAIN, 18));
        distanceTextArea.setEditable(false);
        distanceTextArea.setText(distanceCoveredByFlight(getAllFLightData(fileDataManager)[0][0]));
        timeTextArea = new JTextArea(1, 5);
        timeTextArea.setFont(new Font("times new roman", Font.PLAIN, 18));
        timeTextArea.setEditable(false);
        timeTextArea.setText(timeTakenByFlight(getAllFLightData(fileDataManager)[0][0]));
        fuelConsumptionTextArea = new JTextArea(1, 5);
        fuelConsumptionTextArea.setFont(new Font("times new roman", Font.PLAIN, 18));
        fuelConsumptionTextArea.setEditable(false);
        fuelConsumptionTextArea.setText(fuelConsumedByFlight(getAllFLightData(fileDataManager)[0][0]));
        CO2EmissionTextArea = new JTextArea(1, 5);
        CO2EmissionTextArea.setFont(new Font("times new roman", Font.PLAIN, 18));
        CO2EmissionTextArea.setEditable(false);
        CO2EmissionTextArea.setText(getCO2Emitted(getAllFLightData(fileDataManager)[0][0]));

        JPanel flightInfoJPanel = new JPanel(new GridLayout(8, 2));
        flightInfoJPanel.setPreferredSize(new Dimension((frameWidth * 11) / 100, (frameHeight * 45) / 100));
        flightInfoJPanel.add(distanceJLabel);
        flightInfoJPanel.add(distanceTextArea);
        flightInfoJPanel.add(timeJLabel);
        flightInfoJPanel.add(timeTextArea);
        flightInfoJPanel.add(fuelConsumptionJLabel);
        flightInfoJPanel.add(fuelConsumptionTextArea);
        flightInfoJPanel.add(co2EmittedJLabel);
        flightInfoJPanel.add(CO2EmissionTextArea);
        tablePanel = new JPanel();
        tablePanel.add(flightInfoJPanel);
        tablePanel.setPreferredSize(new Dimension((frameWidth * 12) / 100, frameHeight / 2));

        framePanel.add(tablePanel);


        DefaultTableModel AddFlightTableModel = new DefaultTableModel();
        AddFlightTableModel.setDataVector(new Object[][]{
                {"Choose ...", "", "Choose ...", "Choose ...", "Choose ...", "", ""}
        }, addFlightTableHeader());
        JTable newFlightInfoTable = new JTable(AddFlightTableModel);
        newFlightInfoTable.setRowHeight(25);

        setDropdownItemsOnColumns(fileDataManager, newFlightInfoTable);

        tableScrollPane = new JScrollPane(newFlightInfoTable);

        tableScrollPane.setPreferredSize(new Dimension((frameWidth * 75) / 100, (frameHeight * 10) / 100));
        flightTable.setFillsViewportHeight(true);

        tablePanel = new JPanel();
        tablePanel.add(tableScrollPane);
        tablePanel.setPreferredSize(new Dimension((frameWidth * 77) / 100, frameHeight / 6));
        tablePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(),
                "Add Flights", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("times new roman", Font.PLAIN, 22)));
        framePanel.add(tablePanel);

        tablePanel = new JPanel();
        tablePanel.setPreferredSize(new Dimension((frameWidth * 21) / 100, frameHeight / 8));
        framePanel.add(tablePanel);


        DefaultTableModel newFlightPlanTableModel = new DefaultTableModel();
        newFlightPlanTableModel.setDataVector(new Object[][]{
                {"Choose...", "Choose...", "Choose...", "Choose...", "Choose...", "Choose...", "Choose..."}
        }, new Object[]{"", "", "", "", "", "", ""});

        JTable newFlightPlanTable = new JTable(newFlightPlanTableModel);
        newFlightPlanTable.setRowHeight(25);

        addDropDownItemOnAddFlightPlanTable(fileDataManager, newFlightPlanTable);

        tableScrollPane = new JScrollPane(newFlightPlanTable);
        tableScrollPane.setPreferredSize(new Dimension((frameWidth * 75) / 100, (frameHeight * 10) / 100));
        flightTable.setFillsViewportHeight(true);
        tablePanel = new JPanel();
        tablePanel.add(tableScrollPane);
        tablePanel.setPreferredSize(new Dimension((frameWidth * 77) / 100, frameHeight / 8));
        tablePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(),
                "Flight Plan", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("times new roman", Font.PLAIN, 22)));
        framePanel.add(tablePanel);

        tablePanel = new JPanel();
        tablePanel.setPreferredSize(new Dimension((frameWidth * 21) / 100, frameHeight / 8));
        framePanel.add(tablePanel);


        tablePanel = new JPanel();
        addFlightButton = newButton("Add", frameWidth, frameHeight);
        tablePanel.add(addFlightButton);
        tablePanel.setPreferredSize(new Dimension((frameWidth * 20) / 100, frameHeight / 6));
        framePanel.add(tablePanel);

        tablePanel = new JPanel();
        JButton cancelButton = newButton("Cancel", frameWidth, frameHeight);
        tablePanel.add(cancelButton);
        tablePanel.setPreferredSize(new Dimension((frameWidth * 56) / 100, frameHeight / 6));
        framePanel.add(tablePanel);

        tablePanel = new JPanel();
        JButton exitButton = newButton("Exit", frameWidth, frameHeight);
        tablePanel.add(exitButton);
        tablePanel.setPreferredSize(new Dimension((frameWidth * 21) / 100, frameHeight / 6));
        framePanel.add(tablePanel);

        exitButton.addActionListener(e -> {
            try {
                fileDataManager.writeToFile(flightList);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
            System.exit(0);
        });

        cancelButton.addActionListener(e -> {
            try {
                clearTables(fileDataManager, newFlightInfoTable, newFlightPlanTable);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        addFlightButton.addActionListener(e -> {
            String[][] flightDetails = new String[2][7];
            String flightCodeField = newFlightInfoTable.getValueAt(0, 1).toString();
            if (newFlightInfoTable.getColumnModel().getColumn(5) == null){
                JOptionPane.showMessageDialog(null, "Please make sure flight date is selected.");
                return;
            }
            if (flightCodeField.isEmpty()){
                JOptionPane.showMessageDialog(null, "Please enter a unique flight code.");
                return;
            }
            flightDetails[0] = getTableColumnValues(newFlightInfoTable);
            if (flightDetails[0] == null){
                return;
            }
            flightDetails[1] = getTableColumnValues(newFlightPlanTable);
            if (flightDetails[1] == null)
                return;

            String dateInfo = flightDetails[0][5];

            if (dateInfo != null){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy");
                LocalDateTime dateTime = LocalDateTime.parse(dateInfo, formatter);
                flightDetails[0][5] = dateTime.toLocalDate().toString();
            }
            String[] slicedFlightData = Arrays.copyOfRange(flightDetails[0], 1, flightDetails[0].length);
            String[] flightDataCopy = Arrays.copyOf(slicedFlightData, slicedFlightData.length+1);
            flightDataCopy[slicedFlightData.length] = null;

            boolean noneMatch = Arrays.stream(flightInfo)
                    .noneMatch(da -> da[0].equalsIgnoreCase(flightDataCopy[1]));
            String[][] newCopy;
            if (noneMatch){
                newCopy = Arrays.copyOf(flightInfo, flightInfo.length+1);
                newCopy[flightInfo.length] = flightDataCopy;
                flightInfo = newCopy;
                DefaultTableModel flightTableModel1 = (DefaultTableModel)flightTable.getModel();
                flightTableModel1.addRow(flightDataCopy);
                try {
                    createNewRow(flightList, flightDetails, fileDataManager);
                    clearTables(fileDataManager, newFlightInfoTable, newFlightPlanTable);
                } catch (IOException | DataNotFoundException ex) {
                    System.out.println(ex.getMessage());
                }
            }else {
                JOptionPane.showMessageDialog(null, "Flight already exist.");
            }

        });

        add(framePanel);

        ListSelectionModel flightTableSelectionModel = flightTable.getSelectionModel();


        flightTableSelectionModel.addListSelectionListener(e -> {
            if (!flightTable.getSelectionModel().getValueIsAdjusting()) {
                String code = (String) flightTable.getValueAt(flightTable.getSelectedRow(), 0);
                globalSelectedFlightCode = code;
                String[][] newFlightPlan = new String[0][];
                try {
                    newFlightPlan = getFlightPlanByFlightCode(code);
                } catch (DataNotFoundException ex) {
                    System.out.println(ex.getMessage());
                }
                flightPlanTable.setModel(new DefaultTableModel(newFlightPlan, new String[]{""}));

                distanceTextArea.setText(distanceCoveredByFlight(code));
                timeTextArea.setText(timeTakenByFlight(code));
                CO2EmissionTextArea.setText(getCO2Emitted(code));
                try {
                    fuelConsumptionTextArea.setText(fuelConsumedByFlight(code));
                } catch (DataNotFoundException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
    }

    private void clearTables(FileDataManager dataManager, JTable newFlightDataTable, JTable flightPlanTable) throws IOException{
        newFlightDataTable.setModel(new DefaultTableModel(new Object[][]{
                {"Choose ...", "", "Choose ...", "Choose ...", "Choose ...", "", ""}
        }, addFlightTableHeader()));
        setDropdownItemsOnColumns(dataManager, newFlightDataTable);

        flightPlanTable.setModel(new DefaultTableModel(new Object[][]{
                {"Choose...", "Choose...", "Choose...", "Choose...", "Choose...", "Choose...", "Choose..."}
        }, new Object[]{"", "", "", "", "", "", ""}));
        addDropDownItemOnAddFlightPlanTable(dataManager, flightPlanTable);
    }

    private void createNewRow(List<Flight> flights, String[][] details, FileDataManager dataManager) throws IOException, DataNotFoundException {
        Flight flight = new Flight();
        flight.setId(details[0][1]);
        flight.setDeparture(fetchAirportByFlightCode(dataManager, details[0][3]));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime localDateTime = LocalDateTime
                .parse(details[0][5]+" "+details[0][6], formatter)
                .atZone(ZoneId.of("CET")).toLocalDateTime();
        flight.setDateTime(localDateTime);
        flight.setDestination(fetchAirportByFlightCode(dataManager, details[0][4]));
        flight.setAeroplane(getAeroplaneByModel(dataManager, details[0][2]));
        flight.setAirline(fetchAirlineByName(dataManager, details[0][0]));
            List<Airport> airportList = Arrays.stream(details[1])
                    .filter(code -> !code.contains("Choose"))
                    .map(code -> {
                        try {
                            return fetchAirportByFlightCode(dataManager, code);
                        } catch (IOException | DataNotFoundException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }).filter(Objects::nonNull).collect(Collectors.toList());
            flight.setFlightPlan(new FlightPlan(new LinkedList<>(airportList)));
        flights.add(flight);
    }

    private Airline fetchAirlineByName(FileDataManager manager, String name) throws IOException, DataNotFoundException {
        Optional<Airline> airlineOptional = manager.getAirlines()
                .stream()
                .filter(airline -> airline.getAirlineName().equalsIgnoreCase(name))
                .findFirst();
        if (airlineOptional.isEmpty()){
            throw new DataNotFoundException("Airline not found");
        }
        return airlineOptional.get();
    }

    private Aeroplane getAeroplaneByModel(FileDataManager fileDataManager, String model) throws IOException, DataNotFoundException {
        Optional<Aeroplane> aeroplaneOptional = fileDataManager.getAeroplanes()
                .stream()
                .filter(aeroplane -> aeroplane.getPlaneModel().equalsIgnoreCase(model))
                .findFirst();
        if (aeroplaneOptional.isEmpty()){
            throw new DataNotFoundException("Plane not found");
        }
        return aeroplaneOptional.get();
    }

    private Airport fetchAirportByFlightCode(FileDataManager fileDataManager, String flightCode) throws IOException, DataNotFoundException {
        Optional<Airport> optionalAirport = fileDataManager.getAirports()
                .stream()
                .filter(airport -> airport.getAirportCode().equalsIgnoreCase(flightCode))
                .findFirst();
        if (optionalAirport.isEmpty()){
            throw new DataNotFoundException("Airport not found");
        }
        return optionalAirport.get();
    }

    private String[] getTableColumnValues(JTable table){
        String[] col = new String[7];
        try{
            int count = table.getColumnCount();
            for (int i = 0; i < count; i++){
                col[i] = table.getValueAt(0, i).toString();
            }
            return col;
        }catch (ArrayIndexOutOfBoundsException | NullPointerException aioobe){
            JOptionPane.showMessageDialog(null, "An error occurred. Please enter flight details.");
        }

        return null;
    }

    private JButton newButton(String label, int width, int height){
        JButton jButton = new JButton(label);
        jButton.setBackground(new Color(66, 147, 245));
        jButton.setFont(new Font("times new roman", Font.BOLD, 18));
        jButton.setPreferredSize(new Dimension(width / 10, height / 26));
        jButton.setForeground(Color.WHITE);
        return jButton;
    }

    private void addDropDownItemOnAddFlightPlanTable(FileDataManager dataManager, JTable table) throws IOException {
        for (int i = 0; i < 7; i++) {
            String[] airportCodes = getAirportCodes(dataManager);
            JComboBox<String> jComboBox = new JComboBox<>(airportCodes);
            TableColumn column = table.getColumnModel().getColumn(i);
            column.setCellEditor(new DefaultCellEditor(jComboBox));
        }
    }

    private void setDropdownItemsOnColumns(FileDataManager dataManager, JTable table) throws IOException{
        String[] airlineNames = getAirlineNames(dataManager);
        JComboBox<String> airlines = new JComboBox<>(airlineNames);
        TableColumn airlineCol = table.getColumnModel().getColumn(0);
        airlineCol.setCellEditor(new DefaultCellEditor(airlines));

        String[] planeCodes = getPlaneCodes(dataManager);
        JComboBox<String> planeModelDropDown = new JComboBox<>(planeCodes);
        TableColumn planeModels = table.getColumnModel().getColumn(2);
        planeModels.setCellEditor(new DefaultCellEditor(planeModelDropDown));

        String[] airportCodes = getAirportCodes(dataManager);
        JComboBox<String> departureComboBox = new JComboBox<>(airportCodes);
        TableColumn departureColumn = table.getColumnModel().getColumn(3);
        departureColumn.setCellEditor(new DefaultCellEditor(departureComboBox));

        String[] destinationAirportCodes = getAirportCodes(dataManager);
        JComboBox<String> destinationDropdown = new JComboBox<>(destinationAirportCodes);
        TableColumn destinationColumn = table.getColumnModel().getColumn(4);
        destinationColumn.setCellEditor(new DefaultCellEditor(destinationDropdown));

        JDateChooserCellEditor jDateChooserCellEditor = new JDateChooserCellEditor();
        TableColumn dateColumn = table.getColumnModel().getColumn(5);
        dateColumn.setCellEditor(jDateChooserCellEditor);

        String[] flightTimes = getFlightTimes();
        JComboBox<String> timeSet = new JComboBox<>(flightTimes);
        TableColumn timeColumn = table.getColumnModel().getColumn(6);
        timeColumn.setCellEditor(new DefaultCellEditor(timeSet));
    }

    private String[] getFlightTimes() {
        List<String> flightTimes = new ArrayList<>();
        for (int itr = 0; itr < 24; itr++) {
            int j = 0;
            while (j < 60) {
                String timeFormat = String.format("%02d:%02d", itr, j);
                flightTimes.add(timeFormat);
                j += 10;
            }
        }
        String[] timing = new String[flightTimes.size()];
        return flightTimes.toArray(timing);
    }

    private String[] getAirportCodes(FileDataManager dataManager) throws IOException {
        List<String> airportCodes = dataManager.getAirports()
                .stream()
                .map(Airport::getAirportCode)
                .collect(Collectors.toList());
        String[] airportCodeArray = new String[airportCodes.size()];
        airportCodes.toArray(airportCodeArray);
        return airportCodeArray;
    }

    private String[] getPlaneCodes(FileDataManager manager) {
        String[] aeroplaneCodes = null;
        try {
            List<String> planeModels = manager.getAeroplanes()
                    .stream()
                    .map(Aeroplane::getPlaneModel)
                    .collect(Collectors.toList());
            aeroplaneCodes = new String[planeModels.size()];
            planeModels.toArray(aeroplaneCodes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return aeroplaneCodes;
    }

    private String[] getAirlineNames(FileDataManager dataManager) {
        String[] airlineArray = null;
        try {
            List<String> listOfAirlineNames = dataManager.getAirlines()
                    .stream()
                    .map(Airline::getAirlineName)
                    .collect(Collectors.toList());
            airlineArray = new String[listOfAirlineNames.size()];
            listOfAirlineNames.toArray(airlineArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return airlineArray;
    }

    private String[][] getFlightPlanByFlightCode(String flightCode) throws DataNotFoundException {
        Flight flightWithCode = getFlightWithCode(flightCode);
        List<Airport> airportList = flightWithCode.getFlightPlan().getAirportList();
        flightInfo = new String[airportList.size()][];
        Airport[] airports1 = new Airport[airportList.size()];
        airportList.toArray(airports1);
        for (int i = 0; i < airports1.length; i++) {
            String[] airportInfo = new String[]{
                    airports1[i].getAirportCode(),
            };
            flightInfo[i] = airportInfo;
        }
        return flightInfo;
    }


    private Flight getFlightWithCode(String flightCode) throws DataNotFoundException {
        Optional<Flight> flightOptional = flightList
                .stream()
                .filter(flight1 -> flight1.getId().equalsIgnoreCase(flightCode))
                .findFirst();
        if (flightOptional.isEmpty())
            throw new DataNotFoundException("Flight not found.");
        return flightOptional.get();
    }

    private String distanceCoveredByFlight(String flightCode) {
        String number = null;
        try {
            Flight flightWithCode = getFlightWithCode(flightCode);
            DecimalFormat decimalFormat = new DecimalFormat("###.##");
            number = decimalFormat.format(flightWithCode.distanceTravelled());
        } catch (DataNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return number;
    }

    private String timeTakenByFlight(String flightCode) {
        String number = null;
        try {
            Flight flightWithCode = getFlightWithCode(flightCode);
            DecimalFormat decimalFormat = new DecimalFormat("###.##");
            number = decimalFormat.format(flightWithCode.timeForFlight());
        } catch (DataNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return number;
    }

    private String getCO2Emitted(String flightCode) {
        String number = null;
        try {
            Flight flightWithCode = getFlightWithCode(flightCode);
            DecimalFormat decimalFormat = new DecimalFormat("###.##");
            number = decimalFormat.format(flightWithCode.co2Emitted());
        } catch (DataNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return number;
    }

    private String fuelConsumedByFlight(String flightCode) throws DataNotFoundException {
        Flight flightWithCode = getFlightWithCode(flightCode);
        DecimalFormat decimalFormat = new DecimalFormat("###.##");
        return decimalFormat.format(flightWithCode.consumedFuel());
    }

    private String[][] getAllFLightData(FileDataManager fileDataManager) throws IOException {
        List<Flight> flightList = fileDataManager.allFlights();
        Flight[] flights = new Flight[flightList.size()];
        flightInfo = new String[flightList.size()][];
        flightList.toArray(flights);
        for (int i = 0; i < flights.length; i++) {
            LocalDateTime dateTime = flights[i].getDateTime();
            String[] detail = new String[]{
                    flights[i].getId(),
                    flights[i].getAeroplane().getPlaneModel(),
                    flights[i].getDeparture().getAirportCode(),
                    flights[i].getDestination().getAirportCode(),
                    dateTime.toLocalDate().toString(),
                    dateTime.toLocalTime().toString(),
                    null
            };
            flightInfo[i] = detail;
        }
        return flightInfo;
    }

    String[] flightTableHeader() {
        return new String[]{
                "Flight",
                "Plane",
                "Departure",
                "Destination",
                "Date",
                "Time",
                "Latitude",
                "Longitude",
                "Status"
        };
    }

    private void populateFlightTable(String flightCode, JPanel jPanel) throws DataNotFoundException {
        flightPlanTable = new JTable(getFlightPlanByFlightCode(flightCode), new String[]{""});
        flightPlanTable.setRowHeight(25);
        flightPlanTable.repaint();
        tableScrollPane = new JScrollPane(flightPlanTable);
        tableScrollPane.setPreferredSize(new Dimension((frameWidth * 14) / 100, (frameHeight * 45) / 100));
        flightPlanTable.setFillsViewportHeight(true);
        tablePanel = new JPanel(new GridBagLayout());
        tablePanel.add(tableScrollPane);
        tablePanel.setPreferredSize(new Dimension((frameWidth * 15) / 100, frameHeight / 2));
        tablePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(),
                "Flight Plan", TitledBorder.CENTER, TitledBorder.TOP,
                new Font("times new roman", Font.PLAIN, 20)));
        jPanel.add(tablePanel);
    }

    String[] addFlightTableHeader() {
        return new String[]{
                "Airline",
                "Number",
                "Plane",
                "Departure",
                "Destination",
                "Date",
                "Time"
        };
    }


    public static void main(String[] args) {
        try {
            new App().setVisible(true);
        } catch (IOException | DataNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public synchronized void send(ArrayList<FlightDto> flightDtos) {
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < flightTable.getRowCount(); i++) {
            ids.add(flightTable.getValueAt(i, 0).toString());
        }
        System.out.println(flightDtos.size());
        for (FlightDto flightDto : flightDtos) {
            this.flightDtoHashMap.put(flightDto.getId(), flightDto);
            GPSCoordinate currentGPSCoordinates = flightDto.getCurrentGPSCoordinates();
            Optional<String> stringOptional = ids.stream()
                    .filter(id -> id.equalsIgnoreCase(flightDto.getId()))
                    .findFirst();
            if (stringOptional.isPresent()) {
                System.out.println("Present");
                String getString = stringOptional.get();
                int index = ids.indexOf(getString);
                flightTable.setValueAt(currentGPSCoordinates.getLatitude(), index, flightTable.getColumn("Latitude").getModelIndex());
                flightTable.setValueAt(currentGPSCoordinates.getLongitude(), index, flightTable.getColumn("Longitude").getModelIndex());
            }
            if (globalSelectedFlightCode.equals(flightDto.getId())) {
                distanceTextArea.setText(getCurrentDistanceCovered(globalSelectedFlightCode));
                timeTextArea.setText(getCurrentTimeTaken(globalSelectedFlightCode));
                fuelConsumptionTextArea.setText(getCurrentConsumedFuel(globalSelectedFlightCode));
                CO2EmissionTextArea.setText(getCurrentCO2Emitted(globalSelectedFlightCode));
            }
        }
    }

    private String getCurrentCO2Emitted(String globalSelectedFlightCode) {
        String value = "0.0";
        if (flightDtoHashMap.get(globalSelectedFlightCode) != null) {
            value = String.format("%.4f", this.flightDtoHashMap.get(globalSelectedFlightCode).getCO2Emitted());
        }

        return value;
    }

    private String getCurrentConsumedFuel(String globalSelectedFlightCode) {
        String value = "0.0";
        if (flightDtoHashMap.get(globalSelectedFlightCode) != null) {
            value = String.format("%.4f", this.flightDtoHashMap.get(globalSelectedFlightCode).getCurrentFuelConsumed());
        }

        return value;
    }

    private String getCurrentDistanceCovered(String globalSelectedFlightCode) {
        String value = "0.0";
        System.out.println(flightDtoHashMap.get(globalSelectedFlightCode) != null);
        if (flightDtoHashMap.get(globalSelectedFlightCode) != null) {
            System.out.println(this.flightDtoHashMap.get(globalSelectedFlightCode).getCurrentDistanceFromDeparture());
            value = String.format("%.4f", this.flightDtoHashMap.get(globalSelectedFlightCode).getCurrentDistanceFromDeparture());
        }

        return value;
    }

    private String getCurrentTimeTaken(String globalSelectedFlightCode) {
        String value = "0.0";
        if (flightDtoHashMap.get(globalSelectedFlightCode) != null) {
            value = String.format("%.4f", this.flightDtoHashMap.get(globalSelectedFlightCode).getTimeElapsedInHours());
        }

        return value;
    }
}
