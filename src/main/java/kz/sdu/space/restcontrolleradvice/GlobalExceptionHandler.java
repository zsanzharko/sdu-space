package kz.sdu.space.restcontrolleradvice;

import kz.sdu.space.restcontrolleradvice.responseentity.BasicResponseEntity;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public BasicResponseEntity<?> handleNotFoundException(NotFoundException ex) {
    return new BasicResponseEntity<>("Not found...");
  }
}
