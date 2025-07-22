package ge.bog.realestate.model;

import java.util.List;

public class CompletedApartment extends Apartment {
    public CompletedApartment(String buildingName, int apartmentNumber, double basePrice,
                              ApartmentStatus status, double areaInSqMeters, List<String> features) {
        super(buildingName, apartmentNumber, basePrice, status, areaInSqMeters, features);
    }

    @Override
    public double calculateFinalPrice() {
        return 1.05 * basePrice + features.size() * 200;
    }

    @Override
    public double calculateMaintenanceFee() {
        return areaInSqMeters * 150;
    }
}
