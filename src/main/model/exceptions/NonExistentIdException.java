package model.exceptions;

public class NonExistentIdException extends NotFoundException {

    public NonExistentIdException(int id) {
        super("Entry with id " + id + " wasn't found");
    }
}
