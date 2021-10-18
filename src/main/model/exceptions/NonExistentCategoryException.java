package model.exceptions;

public class NonExistentCategoryException extends NotFoundException {

    public NonExistentCategoryException(String category) {
        super("'" + category + "' category is not found");
    }
}
