package model.exceptions;

// Exception for invalid entry's amount field
public class NegativeAmountException extends EntryFieldException {

    public NegativeAmountException() {
        super("Amount must be > 0");
    }
}
