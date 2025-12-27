package dev.OsRapazes.BatPonto.exception;

public class ApiErrorResponse extends RuntimeException {
  public ApiErrorResponse(String message) {
    super(message);
  }
}
