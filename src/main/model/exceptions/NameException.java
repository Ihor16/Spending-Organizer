package model.exceptions;

public class NameException extends EntryFieldException {

    public NameException(String entryField) {
        super("Entry " + entryField.toLowerCase() + " cannot be empty");
    }
}
