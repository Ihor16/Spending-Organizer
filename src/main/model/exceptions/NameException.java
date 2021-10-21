package model.exceptions;

public class NameException extends EntryFieldException {

    public NameException(String entryField) {
        super("Entry's " + entryField.toLowerCase() + " cannot be blank");
    }
}
