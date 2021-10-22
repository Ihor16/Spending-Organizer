package model.exceptions;

// General exception for invalid entry's fields
public class EntryFieldException extends Exception {

    public EntryFieldException(String message) {
        super(message);
    }
}
