package com.invoice.system.service;

import com.invoice.system.dto.ApproveDto;
import com.invoice.system.dto.InvoiceDto;
import com.invoice.system.dto.InvoiceResponse;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface InvoiceService {
    InvoiceResponse createInvoice(Long customerId, Long purchaseOrderId, InvoiceDto invoiceDto);

    List<InvoiceResponse> getAllInvoice(Long customerId);

    InvoiceResponse getInvoiceById(Long customerId, Long invoiceId);

    void approveInvoice(Long invoiceId, Long customerId, ApproveDto approveDto);

    ByteArrayInputStream generateInvoicePdf(Long customerId, Long invoiceId);
}
