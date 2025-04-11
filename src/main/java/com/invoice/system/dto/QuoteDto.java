package com.invoice.system.dto;

import com.invoice.system.model.QuoteStatus;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class QuoteDto {
    private LocalDate quoteDate;
    private LocalDate validUntil;
    private Double subTotal;
    private Double textAmount;
    private QuoteStatus status;
    private List<ItemDto> items = new ArrayList<>();
}
