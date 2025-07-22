package ge.bog.realestate.exceptions;

public class DuplicateApartmentException extends RuntimeException{
    public DuplicateApartmentException(){
        super("Duplicate Apartment");
    }
}
