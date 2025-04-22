package com.invoice.system.service;

import com.invoice.system.dto.InvoiceDto;
import com.invoice.system.dto.InvoiceResponse;

import java.util.List;

public interface InvoiceService {
    InvoiceResponse createInvoice(Long customerId, Long purchaseOrderId, InvoiceDto invoiceDto);

    List<InvoiceResponse> getAllInvoice(Long customerId);

    InvoiceResponse getInvoiceById(Long customerId, Long invoiceId);
}
