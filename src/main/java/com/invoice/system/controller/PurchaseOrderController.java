package com.invoice.system.controller;

import com.invoice.system.dto.PurchaseOrderDto;
import com.invoice.system.dto.PurchaseOrderResponse;
import com.invoice.system.service.PurchaseOrderService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vendor/{vendorId}")
@RequiredArgsConstructor
public class PurchaseOrderController {

  private final PurchaseOrderService purchaseOrderService;

  @PostMapping("/quote/{quoteId}/purchase-orders")
  public ResponseEntity<PurchaseOrderResponse> createPurchaseOrder(
      @PathVariable Long vendorId,
      @PathVariable Long quoteId,
      @RequestBody PurchaseOrderDto purchaseOrderDto) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(purchaseOrderService.createPurchaseOrder(vendorId, quoteId, purchaseOrderDto));
  }

  @GetMapping("/purchase-orders")
  public ResponseEntity<List<PurchaseOrderResponse>> getAllPurchaseOrders(
      @PathVariable Long vendorId) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(purchaseOrderService.getAllPurchaseOrders(vendorId));
  }

  @GetMapping("/purchase-orders/{purchaseOrderId}")
  public ResponseEntity<PurchaseOrderResponse> getPurchaseOrderById(
      @PathVariable Long vendorId, @PathVariable Long purchaseOrderId) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(purchaseOrderService.getPurchaseOrderById(vendorId, purchaseOrderId));
  }
}
