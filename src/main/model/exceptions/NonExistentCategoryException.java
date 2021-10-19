package model.exceptions;

public class NonExistentCategoryException extends EntryFieldException {

    public NonExistentCategoryException(String category) {
        super("'" + category + "' category is not found");
    }
}
