package com.invoice.system.dto;

import com.invoice.system.model.ItemEntity;
import com.invoice.system.model.QuoteStatus;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class QuoteResponse {
  private Long id;
  private String quoteNumber;
  private LocalDate quoteDate;
  private LocalDate validUntil;
  private List<ItemEntity> items;
  private Double subTotal;
  private Double taxAmount;
  private Double totalAmount;
  private QuoteStatus status;
  private String currency;
}
