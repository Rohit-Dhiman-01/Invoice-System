package com.invoice.system.dto;

import lombok.Data;

@Data
public class ItemDto {
  private String itemName;
  private String description;
  private Integer quantity;
  private Double rate;
  private String hsnCode;
  private Double taxPercent;
  private Double total;
}
