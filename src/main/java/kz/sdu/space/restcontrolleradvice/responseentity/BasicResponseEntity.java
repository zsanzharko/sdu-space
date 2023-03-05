package kz.sdu.space.restcontrolleradvice.responseentity;

import lombok.Data;

import java.util.Date;

@Data
public class BasicResponseEntity<T> {
  private final Date date = new Date();
  private String message = "";
  private T data;

  public BasicResponseEntity(String message) {
    this.message = message;
    this.data = null;
  }

  public BasicResponseEntity(T data) {
    this.data = data;
  }

  public BasicResponseEntity(String message, T data) {
    this.message = message;
    this.data = data;
  }
}
