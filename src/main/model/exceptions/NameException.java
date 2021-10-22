package model.exceptions;

// Exception for invalid entry's title and category
public class NameException extends EntryFieldException {

    public NameException(String entryField) {
        super("Entry's " + entryField.toLowerCase() + " cannot be blank");
    }
}
