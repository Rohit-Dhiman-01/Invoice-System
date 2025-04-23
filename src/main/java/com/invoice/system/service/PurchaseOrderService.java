package com.invoice.system.service;

import com.invoice.system.dto.PurchaseOrderDto;
import com.invoice.system.dto.PurchaseOrderResponse;
import java.util.List;

public interface PurchaseOrderService {
  PurchaseOrderResponse createPurchaseOrder(
      Long vendorId, Long quoteId, PurchaseOrderDto purchaseOrderDto);

  List<PurchaseOrderResponse> getAllPurchaseOrders(Long vendorId);

  PurchaseOrderResponse getPurchaseOrderById(Long vendorId, Long purchaseOrderId);

  byte[] generatePurchaseOrderPdf(Long vendorId, Long purchaseOrderId);
}
