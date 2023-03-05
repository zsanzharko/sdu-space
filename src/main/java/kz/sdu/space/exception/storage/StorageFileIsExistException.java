package kz.sdu.space.exception.storage;

public class StorageFileIsExistException extends RuntimeException {

  public StorageFileIsExistException(String message) {
    super(message);
  }
}
