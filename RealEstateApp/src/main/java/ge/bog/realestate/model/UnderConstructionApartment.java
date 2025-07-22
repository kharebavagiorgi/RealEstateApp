package ge.bog.realestate.model;

import java.util.List;

public class UnderConstructionApartment extends Apartment {
    public UnderConstructionApartment(String buildingName, int apartmentNumber, double basePrice,
                                      ApartmentStatus status, double areaInSqMeters, List<String> features) {
        super(buildingName, apartmentNumber, basePrice, status, areaInSqMeters, features);
    }

    @Override
    public double calculateFinalPrice() {
        return 0.9 * basePrice - areaInSqMeters * 10;
    }

    @Override
    public double calculateMaintenanceFee() {
        return 400;
    }
}
