package com.invoice.system.config.exception;

public class PurchaseOrderNotFoundException extends RuntimeException {
  public PurchaseOrderNotFoundException(String message) {
    super(message);
  }
}
