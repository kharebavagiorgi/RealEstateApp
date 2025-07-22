package ge.bog.realestate.model;

import java.time.LocalDate;

public class SaleRecord <T extends Apartment>{

    T apartment;
    double salePrice;
    Buyer buyer;
    LocalDate saleDateTime;

    public SaleRecord(T apartment, double salePrice, Buyer buyer, LocalDate saleDateTime) {
        this.apartment = apartment;
        this.salePrice = salePrice;
        this.buyer = buyer;
        this.saleDateTime = saleDateTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Sale Date: ").append(saleDateTime).append("\n");
        sb.append("Apartment: ").append(apartment.toString());
        sb.append("Sale Price: $").append(String.format("%.2f", salePrice)).append("\n");
        sb.append("Buyer: ").append(buyer.toString()).append("\n");
        return sb.toString();
    }


    public boolean isWithinLast30Days(){
        return !saleDateTime.isBefore(LocalDate.now().minusDays(30)) && !saleDateTime.isAfter(LocalDate.now());
    }

    public void exportToFile(String filename) {
        try (java.io.FileWriter writer = new java.io.FileWriter(filename, true)) {
            writer.write(this.toString());
            writer.write("\n");
            System.out.println("Sale record exported to " + filename);
        } catch (Exception e) {
            System.err.println("Failed to export sale record: " + e.getMessage());
        }
    }


}
