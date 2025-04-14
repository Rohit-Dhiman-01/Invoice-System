package com.invoice.system.config.exception;

public class QuoteNotFoundException extends RuntimeException {
  public QuoteNotFoundException(String message) {
    super(message);
  }
}
