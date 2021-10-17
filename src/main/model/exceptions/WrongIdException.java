package model.exceptions;

public class WrongIdException extends Exception {

    public WrongIdException(int id) {
        super("Entry with id " + id + " wasn't found");
    }
}
