package kz.sdu.space.exception.storage;

public class StorageNotFoundException extends RuntimeException {

  public StorageNotFoundException(String message) {
    super(message);
  }
}
