package ge.bog.realestate.model;

import ge.bog.realestate.exceptions.InvalidBuyerException;

import java.time.LocalDate;

public class Buyer {

    public static int idCounter = 1;

    String name;
    String contact;
    LocalDate registrationDate;
    String idNumber;

    public Buyer(String name, String contact, LocalDate registrationDate) {
        this.name = name;
        this.contact = contact;
        this.registrationDate = registrationDate;

        if(registrationDate.isAfter(LocalDate.now())) {
            throw new InvalidBuyerException();
        }

        idNumber = "Buyer" + idCounter++;
    }

    public String toString(){
        return name + " (" + contact + ")";
    }

    public String getContact() {
        return contact;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public String getName() {
        return name;
    }

}
