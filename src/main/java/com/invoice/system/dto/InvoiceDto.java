package com.invoice.system.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class InvoiceDto {
    private LocalDate invoiceDate;
    private LocalDate dueDate;
}
