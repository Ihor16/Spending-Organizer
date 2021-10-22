package model.exceptions;

// General exception for invalid record's fields
public class RecordFieldException extends Exception {

    public RecordFieldException(String message) {
        super(message);
    }
}
