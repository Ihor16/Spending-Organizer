package model.exceptions;

public class NegativeAmountException extends EntryFieldException {

    public NegativeAmountException() {
        super("Amount cannot be <= 0");
    }
}
