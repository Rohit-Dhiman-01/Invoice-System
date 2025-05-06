package com.invoice.system.service.impl;

import com.invoice.system.config.exception.InvoiceNotFoundException;
import com.invoice.system.config.exception.PurchaseOrderNotFoundException;
import com.invoice.system.config.exception.QuoteNotFoundException;
import com.invoice.system.model.InvoiceEntity;
import com.invoice.system.model.PurchaseOrderEntity;
import com.invoice.system.model.QuoteEntity;
import com.invoice.system.repository.InvoiceRepository;
import com.invoice.system.repository.PurchaseOrderRepository;
import com.invoice.system.repository.QuoteRepository;
import com.invoice.system.service.InvoiceService;
import com.invoice.system.service.PdfService;
import com.invoice.system.service.PurchaseOrderService;
import com.invoice.system.service.QuoteService;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PdfServiceImpl implements PdfService {

  private final InvoiceRepository invoiceRepository;
  private final QuoteRepository quoteRepository;
  private final PurchaseOrderRepository purchaseOrderRepository;
  private final QuoteService quoteService;
  private final InvoiceService invoiceService;
  private final PurchaseOrderService purchaseOrderService;

  @Override
  public ByteArrayInputStream generatePdf(String id) {
    String key = Arrays.stream(id.split("-")).findFirst().get();
    if (key.equalsIgnoreCase("INV")) {
      InvoiceEntity invoiceEntity =
          invoiceRepository
              .findByInvoiceNumberIgnoreCase(id)
              .orElseThrow(() -> new InvoiceNotFoundException("invoice not found "));
      return invoiceService.generateInvoicePdf(
          invoiceEntity.getCustomer().getId(), invoiceEntity.getId());
    } else if (key.equalsIgnoreCase("PO")) {
      PurchaseOrderEntity purchaseOrderEntity =
          purchaseOrderRepository
              .findByPoNumberIgnoreCase(id)
              .orElseThrow(() -> new PurchaseOrderNotFoundException("Purchase Order Not Found "));
      return purchaseOrderService.generatePurchaseOrderPdf(
          purchaseOrderEntity.getVendor().getId(), purchaseOrderEntity.getId());
    } else if (key.equalsIgnoreCase("Q")) {
      QuoteEntity quoteEntity =
          quoteRepository
              .findByQuoteNumberIgnoreCase(id)
              .orElseThrow(() -> new QuoteNotFoundException("Quote Not Found "));
      return quoteService.generateQuotePdf(quoteEntity.getCustomer().getId(), quoteEntity.getId());
    } else {
      throw new RuntimeException("Invalid id " + id);
    }
  }

  @Override
  public String getDocumentNumber(String id) {
    String key = Arrays.stream(id.split("-")).findFirst().get();
    if (key.equalsIgnoreCase("INV")) {
      return invoiceRepository
          .findByInvoiceNumberIgnoreCase(id)
          .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found"))
          .getInvoiceNumber();
    } else if (key.equalsIgnoreCase("PO")) {
      return purchaseOrderRepository
          .findByPoNumberIgnoreCase(id)
          .orElseThrow(() -> new PurchaseOrderNotFoundException("PO not found"))
          .getPoNumber();
    } else if (key.equalsIgnoreCase("Q")) {
      return quoteRepository
          .findByQuoteNumberIgnoreCase(id)
          .orElseThrow(() -> new QuoteNotFoundException("Quote not found"))
          .getQuoteNumber();
    } else {
      throw new RuntimeException("Invalid id " + id);
    }
  }
}
