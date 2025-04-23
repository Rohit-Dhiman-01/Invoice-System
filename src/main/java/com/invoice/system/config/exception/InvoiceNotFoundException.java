package com.invoice.system.config.exception;

public class InvoiceNotFoundException extends RuntimeException {
  public InvoiceNotFoundException(String message) {
    super(message);
  }
}
