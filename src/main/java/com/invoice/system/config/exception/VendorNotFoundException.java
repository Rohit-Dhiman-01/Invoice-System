package com.invoice.system.config.exception;

public class VendorNotFoundException extends RuntimeException {
  public VendorNotFoundException(String message) {
    super(message);
  }
}
