package com.invoice.system.controller;

import com.invoice.system.dto.ApproveDto;
import com.invoice.system.dto.InvoiceDto;
import com.invoice.system.dto.InvoiceResponse;
import com.invoice.system.service.impl.InvoiceServiceImpl;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer/{customerId}")
@RequiredArgsConstructor
public class InvoiceController {

  @Autowired private InvoiceServiceImpl invoiceService;

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
      @RequestBody ApproveDto approveDto) {
    invoiceService.approveInvoice(invoiceId, customerId, approveDto);
    return ResponseEntity.status(HttpStatus.OK).build();
  }
}
