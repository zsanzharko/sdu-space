package kz.sdu.space.exception;

public class IdNotFoundException extends RuntimeException {

  public IdNotFoundException() {
    super("Id not found.");
  }

  public IdNotFoundException(String message) {
    super(message);
  }
}
