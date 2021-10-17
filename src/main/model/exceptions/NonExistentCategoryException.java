package model.exceptions;

public class NonExistentCategoryException extends NotFoundException {

    public NonExistentCategoryException() {
        super("Entered category is not found");
    }
}
