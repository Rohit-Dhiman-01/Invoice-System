package com.invoice.system.dto;

import com.invoice.system.model.InvoiceEntityPaymentStatus;
import com.invoice.system.model.ItemEntity;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class InvoiceResponse {
  private Long id;
  private String invoiceNumber;
  private LocalDate invoiceDate;
  private LocalDate dueDate;
  private List<ItemEntity> items;
  private Double subTotal;
  private Double taxAmount;
  private Double totalAmount;
  private Double dueAmount;
  private InvoiceEntityPaymentStatus paymentStatus;
  private String currency;
}
