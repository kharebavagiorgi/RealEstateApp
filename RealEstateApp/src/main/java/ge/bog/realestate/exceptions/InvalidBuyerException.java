package ge.bog.realestate.exceptions;

public class InvalidBuyerException extends RuntimeException{
    public InvalidBuyerException(){
        super("Invalid Buyer");
    }

    public InvalidBuyerException(String message){
        super(message);
    }

}
