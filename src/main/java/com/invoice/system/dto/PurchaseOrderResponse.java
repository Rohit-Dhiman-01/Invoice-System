package com.invoice.system.dto;

import com.invoice.system.model.ItemEntity;
import com.invoice.system.model.PurchaseOrderStatus;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class PurchaseOrderResponse {
  private Long id;
  private String poNumber;
  private LocalDate poDate;
  private String shippingAddress;
  private List<ItemEntity> items;
  private Double subTotal;
  private Double taxAmount;
  private Double totalAmount;
  private PurchaseOrderStatus status;
  private String currency;
}
