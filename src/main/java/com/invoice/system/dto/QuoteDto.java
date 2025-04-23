package com.invoice.system.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.invoice.system.model.QuoteStatus;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class QuoteDto {
  private LocalDate quoteDate;
  private LocalDate validUntil;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Double subTotal;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Double taxAmount;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private QuoteStatus status;

  private String currency;

  private List<ItemDto> items = new ArrayList<>();
}
