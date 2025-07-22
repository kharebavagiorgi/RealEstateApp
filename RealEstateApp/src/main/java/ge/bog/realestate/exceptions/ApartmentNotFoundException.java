package ge.bog.realestate.exceptions;

public class ApartmentNotFoundException extends RuntimeException {
    public ApartmentNotFoundException() {
        super("Apartment not found");
    }
}
