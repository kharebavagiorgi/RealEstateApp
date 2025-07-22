package ge.bog.realestate.exceptions;

public class AlreadySoldException extends RuntimeException{
    public AlreadySoldException(){
        super("Already Sold");
    }


}
