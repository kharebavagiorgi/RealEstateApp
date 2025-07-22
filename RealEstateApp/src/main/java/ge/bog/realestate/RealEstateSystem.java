package ge.bog.realestate;

import ge.bog.realestate.exceptions.*;
import ge.bog.realestate.model.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class RealEstateSystem {

    public static void main(String[] args) {
        ApartmentManager apartmentManager = new ApartmentManager();

        Buyer buyer1 = new Buyer("Giorgi Kharebava", "551", LocalDate.of(2025, 7, 22));
        Buyer buyer2 = new Buyer("Sxva Giorgi Kharebava", "551", LocalDate.of(2025, 7, 22));

        Apartment apartment1 = new UnderConstructionApartment("Building A", 101, 100000, ApartmentStatus.UNDER_CONSTRUCTION, 50, List.of("Pool"));
        Apartment apartment2 = new CompletedApartment("Building B", 102, 120000, ApartmentStatus.COMPLETED, 65, Arrays.asList("Garden", "Parking"));

        try {
            apartmentManager.addBuyer(buyer1);
            apartmentManager.addBuyer(buyer2);
            apartmentManager.addApartment(apartment1);
            apartmentManager.addApartment(apartment2);
        } catch (InvalidBuyerException | DuplicateApartmentException e) {
            System.out.println(e.getMessage());
        }

        try {
            apartmentManager.addBuyer(buyer1);
        } catch (InvalidBuyerException e) {
            System.out.println("Error: " + e.getMessage());
        }


        try {
            apartmentManager.addApartment(apartment1);
        } catch (DuplicateApartmentException e) {
            System.out.println("Error: " + e.getMessage());
        }


        try {
            apartmentManager.sellApartment("Building A", 101, new Buyer("Unregistered Buyer", "555-9999", LocalDate.of(2023, 1, 15)));
        } catch (InvalidBuyerException e) {
            System.out.println("Error: " + e.getMessage());
        }


        try {
            apartmentManager.sellApartment("Building A", 101, buyer1);
            apartmentManager.sellApartment("Building A", 101, buyer2);
        } catch (AlreadySoldException | ApartmentNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }


        try {
            apartmentManager.sellApartment("Building A", 101, buyer1);
            apartmentManager.sellApartment("Building A", 102, buyer2);
        } catch (AlreadySoldException | ApartmentNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }


        apartmentManager.undoSale("Building A", 101);


        System.out.println("\nApartments grouped by status:");
        apartmentManager.displayApartmentsByStatus();


        apartmentManager.showStatistics(true);


        apartmentManager.exportToFile("sales_report.txt");
    }
}
