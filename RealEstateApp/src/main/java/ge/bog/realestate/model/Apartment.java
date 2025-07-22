package ge.bog.realestate.model;

import java.util.List;

public abstract class Apartment implements Exportable {

    String buildingName;
    int apartmentNumber;
    String ownerName;
    double basePrice;
    ApartmentStatus status;
    boolean isSold;
    double areaInSqMeters;
    List<String> features;

    public Apartment(String buildingName, int apartmentNumber, double basePrice,
                     ApartmentStatus status, double areaInSqMeters, List<String> features) {
        this.buildingName = buildingName;
        this.apartmentNumber = apartmentNumber;
        this.ownerName = "unsold";
        this.basePrice = basePrice;
        this.status = status;
        this.isSold = false;
        this.areaInSqMeters = areaInSqMeters;
        this.features = features;
    }

    public abstract double calculateFinalPrice();
    public abstract double calculateMaintenanceFee();

    public void markAsSold(Buyer buyer){
        this.ownerName = buyer.name;
        this.isSold = true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Apartment #").append(apartmentNumber)
                .append(" in ").append(buildingName).append("\n");
        sb.append("Status: ").append(status).append("\n");
        sb.append("Owner: ").append(ownerName).append("\n");
        sb.append("Area: ").append(areaInSqMeters).append(" sq meters\n");
        sb.append("Base Price: $").append(String.format("%.2f", basePrice)).append("\n");
        sb.append("Final Price: $").append(String.format("%.2f", calculateFinalPrice())).append("\n");
        sb.append("Maintenance Fee: $").append(String.format("%.2f", calculateMaintenanceFee())).append("\n");
        sb.append("Features: ").append(features.isEmpty() ? "None" : String.join(", ", features)).append("\n");
        sb.append("Sold: ").append(isSold ? "Yes" : "No").append("\n");
        return sb.toString();
    }


    @Override
    public void exportToFile(String filename) {
        try (java.io.FileWriter writer = new java.io.FileWriter(filename, true)) {
            writer.write(this.toString());
            writer.write("\n");
            System.out.println("Apartment info exported to " + filename);
        } catch (Exception e) {
            System.err.println("Failed to export apartment: " + e.getMessage());
        }
    }

}
