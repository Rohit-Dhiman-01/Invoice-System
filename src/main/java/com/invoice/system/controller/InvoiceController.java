package com.invoice.system.controller;

import com.invoice.system.dto.ApprovePaymentDto;
import com.invoice.system.dto.InvoiceDto;
import com.invoice.system.dto.InvoiceResponse;
import com.invoice.system.service.InvoiceService;
import jakarta.validation.Valid;
import java.io.ByteArrayInputStream;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer/{customerId}")
@RequiredArgsConstructor
public class InvoiceController {

  @Autowired private InvoiceService invoiceService;

  @PostMapping("purchase-orders/{purchaseOrderId}/invoices")
  public ResponseEntity<InvoiceResponse> createInvoice(
      @Valid @RequestBody InvoiceDto invoiceDto,
      @PathVariable Long customerId,
      @PathVariable Long purchaseOrderId) {
    return new ResponseEntity<>(
        invoiceService.createInvoice(customerId, purchaseOrderId, invoiceDto), HttpStatus.CREATED);
  }

  @GetMapping("/invoices")
  public ResponseEntity<List<InvoiceResponse>> getAllInvoice(@PathVariable Long customerId) {
    return new ResponseEntity<>(invoiceService.getAllInvoice(customerId), HttpStatus.OK);
  }

  @GetMapping("/invoices/{invoiceId}")
  public ResponseEntity<InvoiceResponse> getInvoiceById(
      @PathVariable Long customerId, @PathVariable Long invoiceId) {
    return new ResponseEntity<>(
        invoiceService.getInvoiceById(customerId, invoiceId), HttpStatus.OK);
  }

  @PutMapping("/invoices/{invoiceId}/status")
  public ResponseEntity<Void> approveInvoice(
      @PathVariable Long customerId,
      @PathVariable Long invoiceId,
      @RequestBody ApprovePaymentDto approvePaymentDto) {
    invoiceService.approveInvoice(invoiceId, customerId, approvePaymentDto);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @GetMapping("/invoices/{invoiceId}/pdf")
  public ResponseEntity<InputStreamResource> generateInvoicePDF(
      @PathVariable Long customerId, @PathVariable Long invoiceId) {
    ByteArrayInputStream pdfStream = invoiceService.generateInvoicePdf(customerId, invoiceId);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=invoice.pdf")
        .contentType(MediaType.APPLICATION_PDF)
        .body(new InputStreamResource(pdfStream));
  }
}
