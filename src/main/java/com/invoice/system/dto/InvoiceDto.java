package com.invoice.system.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class InvoiceDto {
  private LocalDate invoiceDate;
  private LocalDate dueDate;
}
