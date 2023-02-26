package kz.sdu.space.restcontrolleradvice;

import kz.sdu.space.exception.IdNotFoundException;
import kz.sdu.space.exception.InvalidInputException;
import kz.sdu.space.restcontrolleradvice.responseentity.BasicResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(value = "kz.sdu.space.component.event")
public class EventRestControllerAdvice {

    @ExceptionHandler(value = {InvalidInputException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BasicResponseEntity<?> invalidInput(RuntimeException ex) {
      String message = ex.getMessage();
      return new BasicResponseEntity<>(message);
    }

  @ExceptionHandler(value = {IdNotFoundException.class})
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public BasicResponseEntity<?> idNotFound(RuntimeException ex) {
    String message = ex.getMessage();
    return new BasicResponseEntity<>(message);
  }
}
