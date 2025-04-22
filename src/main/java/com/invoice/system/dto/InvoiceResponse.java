package com.invoice.system.dto;

import com.invoice.system.model.InvoiceEntityPaymentStatus;
import com.invoice.system.model.ItemEntity;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class InvoiceResponse {
    private Long id;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private Double subTotal;
    private Double taxAmount;
    private Double totalAmount;
    private InvoiceEntityPaymentStatus paymentStatus;
    private List<ItemEntity> items;
}
