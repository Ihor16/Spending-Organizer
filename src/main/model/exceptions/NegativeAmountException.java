package model.exceptions;

public class NegativeAmountException extends EntryFieldException {

    public NegativeAmountException() {
        super("Amount must be > 0");
    }
}
