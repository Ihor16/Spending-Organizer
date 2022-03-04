package com.ihor.spendingorganizer.model.exceptions;

// Exception for invalid record's title and category
public class NameException extends RecordFieldException {

    public NameException() {
        super("Such category already exists");
    }

    public NameException(String recordField) {
        super("Record's " + recordField.toLowerCase() + " cannot be blank");
    }
}
