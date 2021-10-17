package model.exceptions;

public class NotFoundCategoryException extends EntryFieldException {

    public NotFoundCategoryException() {
        super("Entered category is not found");
    }
}
