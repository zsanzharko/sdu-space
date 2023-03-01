package kz.sdu.space.exception.storage;

public class StorageItemNotFoundException extends StorageException {

  public StorageItemNotFoundException() {
    super("Item in storage not found.");
  }
}
