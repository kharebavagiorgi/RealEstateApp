package ge.bog.realestate.model;

import ge.bog.realestate.exceptions.AlreadySoldException;
import ge.bog.realestate.exceptions.ApartmentNotFoundException;
import ge.bog.realestate.exceptions.DuplicateApartmentException;
import ge.bog.realestate.exceptions.InvalidBuyerException;

import java.time.LocalDate;
import java.util.*;
import java.util.logging.*;
import java.util.stream.Collectors;

public class ApartmentManager {
    private static final Logger logger = Logger.getLogger(ApartmentManager.class.getName());

    Map<String, Map<Integer, Apartment>> buildings;
    List<SaleRecord<? extends Apartment>> sales;
    Set<String> buyerIdNumbers;
    Map<String, Buyer> buyers;

    public ApartmentManager() {
        buildings = new HashMap<>();
        sales = new ArrayList<>();
        buyerIdNumbers = new HashSet<>();
        buyers = new HashMap<>();

        try {
            LogManager.getLogManager().reset();

            FileHandler fileHandler = new FileHandler("apartment_manager.log", true);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL);
            logger.info("ApartmentManager initialized.");
        } catch (Exception e) {
            System.out.println("Error setting up logger: " + e.getMessage());
        }
    }

    public void addApartment(Apartment apartment) {
        logger.info("Attempting to add apartment: " + apartment);
        if (apartment == null) {
            logger.severe("Apartment is null");
            throw new RuntimeException("Apartment is null");
        }

        if (!buildings.containsKey(apartment.buildingName)) {
            buildings.put(apartment.buildingName, new HashMap<Integer, Apartment>());
            buildings.get(apartment.buildingName).put(apartment.apartmentNumber, apartment);
            logger.info("Apartment added to building: " + apartment.buildingName + " #" + apartment.apartmentNumber);
        } else {
            logger.warning("Duplicate apartment attempt for: " + apartment.buildingName + " #" + apartment.apartmentNumber);
            throw new DuplicateApartmentException();
        }
    }

    public void addBuyer(Buyer buyer) {
        logger.info("Attempting to add buyer: " + buyer);
        if (buyer == null) {
            logger.severe("Buyer is null");
            throw new InvalidBuyerException("Buyer is null");
        }

        String id = buyer.getIdNumber();

        if (buyerIdNumbers.contains(id)) {
            logger.warning("Buyer already exists: " + buyer);
            throw new InvalidBuyerException("Buyer already exists");
        }

        buyerIdNumbers.add(id);
        buyers.put(id, buyer);
        logger.info("Buyer added: " + buyer);
    }

    public void sellApartment(String buildingName, int apartmentNumber, Buyer buyer) {
        logger.info("Attempting to sell apartment: " + buildingName + " #" + apartmentNumber + " to buyer: " + buyer);

        if (!buyers.containsKey(buyer.getIdNumber())) {
            logger.severe("Buyer not registered: " + buyer);
            throw new InvalidBuyerException("Buyer is not registered");
        }

        Map<Integer, Apartment> apartments = buildings.get(buildingName);
        if (apartments == null || !apartments.containsKey(apartmentNumber)) {
            logger.severe("Apartment not found: " + buildingName + " #" + apartmentNumber);
            throw new ApartmentNotFoundException();
        }

        Apartment apartment = apartments.get(apartmentNumber);

        if (apartment.isSold) {
            logger.warning("Apartment already sold: " + apartment);
            throw new AlreadySoldException();
        }

        double finalPrice = apartment.calculateFinalPrice();
        SaleRecord<? extends Apartment> sr = new SaleRecord<>(apartment, finalPrice, buyer, LocalDate.now());
        sales.add(sr);

        apartment.markAsSold(buyer);

        logger.info("Apartment has been sold: " + apartment);
    }

    public void undoSale(String buildingName, int apartmentNumber) {
        logger.info("Attempting to undo sale of apartment: " + buildingName + " #" + apartmentNumber);

        Map<Integer, Apartment> apartments = buildings.get(buildingName);
        if (apartments == null || !apartments.containsKey(apartmentNumber)) {
            logger.severe("Apartment not found: " + buildingName + " #" + apartmentNumber);
            throw new ApartmentNotFoundException();
        }

        Apartment apartment = apartments.get(apartmentNumber);

        if (!apartment.isSold) {
            logger.severe("Apartment has not been sold yet: " + apartment);
            throw new RuntimeException("Apartment has not been sold.");
        }

        Iterator<SaleRecord<? extends Apartment>> iterator = sales.iterator();
        while (iterator.hasNext()) {
            SaleRecord<? extends Apartment> sale = iterator.next();
            if (sale.apartment == apartment) {
                iterator.remove();
                break;
            }
        }

        apartment.isSold = false;
        apartment.ownerName = "UNSOLD";

        logger.info("Sale undone for apartment: " + apartment);
    }

    public void displayApartmentsByStatus() {
        System.out.println("Displaying apartments by status...");

        buildings.values().stream()
                .flatMap(b -> b.values().stream())
                .collect(Collectors.groupingBy(a -> a.status))
                .forEach((status, apartments) -> {
                    System.out.println("Status: " + status);
                    apartments.stream()
                            .sorted(Comparator.comparingInt(a -> a.apartmentNumber))
                            .forEach(a -> System.out.println(a.toString()));
                });
    }

    public void showStatistics() {
        System.out.println("Generating real estate statistics...");

        long totalApartments = buildings.values().stream()
                .mapToLong(b -> b.size()).sum();

        double totalRevenue = sales.stream()
                .mapToDouble(s -> s.salePrice).sum();

        double avgPrice = sales.stream()
                .mapToDouble(s -> s.salePrice).average().orElse(0);

        Optional<SaleRecord<? extends Apartment>> maxSale = sales.stream()
                .max(Comparator.comparingDouble(s -> s.salePrice));

        Optional<SaleRecord<? extends Apartment>> minSale = sales.stream()
                .min(Comparator.comparingDouble(s -> s.salePrice));

        long recentSales = sales.stream()
                .filter(SaleRecord::isWithinLast30Days).count();

        System.out.printf("""
        === Real Estate Statistics ===
        Total Apartments: %d
        Apartments Sold: %d
        Total Revenue: $%.2f
        Average Sale Price: $%.2f
        """, totalApartments, sales.size(), totalRevenue, avgPrice);

        maxSale.ifPresent(s -> System.out.println("Most Expensive Sale: $" +
                String.format("%.2f", s.salePrice) + " -> " + s.apartment.buildingName +
                " #" + s.apartment.apartmentNumber + " (" + s.buyer.name + ")"));

        minSale.ifPresent(s -> System.out.println("Least Expensive Sale: $" +
                String.format("%.2f", s.salePrice) + " -> " +
                s.apartment.buildingName + " #" + s.apartment.apartmentNumber + " (" +
                s.buyer.name + ")"));

        System.out.println("Sales in Last 30 Days: " + recentSales);
    }

    public void showStatistics(boolean saveToFile) {
        System.out.println("Generating real estate statistics... Save to file: " + saveToFile);

        long totalApartments = buildings.values().stream()
                .mapToLong(b -> b.size()).sum();

        double totalRevenue = sales.stream()
                .mapToDouble(s -> s.salePrice).sum();

        double avgPrice = sales.stream()
                .mapToDouble(s -> s.salePrice).average().orElse(0);

        Optional<SaleRecord<? extends Apartment>> maxSale = sales.stream()
                .max(Comparator.comparingDouble(s -> s.salePrice));

        Optional<SaleRecord<? extends Apartment>> minSale = sales.stream()
                .min(Comparator.comparingDouble(s -> s.salePrice));

        long recentSales = sales.stream()
                .filter(SaleRecord::isWithinLast30Days).count();

        StringBuilder sb = new StringBuilder();
        sb.append("=== Real Estate Statistics ===\n");
        sb.append("Total Apartments: ").append(totalApartments).append("\n");
        sb.append("Apartments Sold: ").append(sales.size()).append("\n");
        sb.append("Total Revenue: $").append(String.format("%.2f", totalRevenue)).append("\n");
        sb.append("Average Sale Price: $").append(String.format("%.2f", avgPrice)).append("\n");

        maxSale.ifPresent(s -> sb.append("Most Expensive Sale: $")
                .append(String.format("%.2f", s.salePrice)).append(" -> ")
                .append(s.apartment.buildingName).append(" #")
                .append(s.apartment.apartmentNumber).append(" (")
                .append(s.buyer.name).append(")\n"));

        minSale.ifPresent(s -> sb.append("Least Expensive Sale: $")
                .append(String.format("%.2f", s.salePrice)).append(" -> ")
                .append(s.apartment.buildingName).append(" #")
                .append(s.apartment.apartmentNumber).append(" (")
                .append(s.buyer.name).append(")\n"));

        sb.append("Sales in Last 30 Days: ").append(recentSales).append("\n");

        if (saveToFile) {
            try (java.io.FileWriter writer = new java.io.FileWriter("sales_report.txt")) {
                writer.write(sb.toString());
                System.out.println("Statistics saved to sales_report.txt");
            } catch (Exception e) {
                System.out.println("Failed to write report: " + e.getMessage());
            }
        }
    }

    public void exportToFile(String filename) {
        System.out.println("Exporting data to file: " + filename);

        StringBuilder sb = new StringBuilder();
        sb.append("=== Apartment Listings ===\n");

        buildings.forEach((buildingName, apartmentsMap) -> {
            sb.append("Building: ").append(buildingName).append("\n");
            apartmentsMap.values().stream()
                    .sorted(Comparator.comparingInt(a -> a.apartmentNumber))
                    .forEach(apartment -> sb.append(apartment.toString()).append("\n"));
        });

        sb.append("\n=== Sale Records ===\n");

        sales.stream()
                .sorted(Comparator.comparing(s -> s.saleDateTime))
                .forEach(sale -> sb.append(sale.toString()).append("\n"));

        try (java.io.FileWriter writer = new java.io.FileWriter(filename)) {
            writer.write(sb.toString());
            System.out.println("Data exported to file: " + filename);
        } catch (Exception e) {
            System.out.println("Failed to export data: " + e.getMessage());
        }
    }
}
