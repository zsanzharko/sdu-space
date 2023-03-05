package kz.sdu.space.restcontrolleradvice;

import kz.sdu.space.exception.storage.StorageException;
import kz.sdu.space.exception.storage.StorageItemNotFoundException;
import kz.sdu.space.exception.storage.StorageNotFoundException;
import kz.sdu.space.restcontrolleradvice.responseentity.BasicResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class StorageRestControllerAdvice {

  @ExceptionHandler(value = {StorageException.class, StorageNotFoundException.class})
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public BasicResponseEntity<?> handlerStorageService() {
    return new BasicResponseEntity<>("Have problem with storage...");
  }

  @ExceptionHandler(value = {StorageItemNotFoundException.class})
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public BasicResponseEntity<?> storageNotFoundException(RuntimeException ex) {
    final String message = ex.getMessage();
    return new BasicResponseEntity<>(message);
  }
}
