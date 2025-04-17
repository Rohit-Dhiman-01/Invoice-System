package com.invoice.system.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class PurchaseOrderDto {
  private LocalDate poDate;
  private String shippingAddress;
}
