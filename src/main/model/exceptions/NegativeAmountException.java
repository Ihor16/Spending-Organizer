package model.exceptions;

// Exception for invalid record's amount field
public class NegativeAmountException extends RecordFieldException {

    public NegativeAmountException() {
        super("Amount must be > 0");
    }
}
