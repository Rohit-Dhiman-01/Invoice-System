package com.invoice.system.config.exception;

public class QuoteAlreadySentException extends RuntimeException {
  public QuoteAlreadySentException(String message) {
    super(message);
  }
}
