package com.invoice.system.service;

import com.invoice.system.dto.ApproveDto;
import com.invoice.system.dto.PurchaseOrderDto;
import com.invoice.system.dto.PurchaseOrderResponse;
import java.io.ByteArrayInputStream;
import java.util.List;

public interface PurchaseOrderService {
  PurchaseOrderResponse createPurchaseOrder(
      Long vendorId, Long quoteId, PurchaseOrderDto purchaseOrderDto);

  List<PurchaseOrderResponse> getAllPurchaseOrders(Long vendorId);

  PurchaseOrderResponse getPurchaseOrderById(Long vendorId, Long purchaseOrderId);

  ByteArrayInputStream generatePurchaseOrderPdf(Long vendorId, Long purchaseOrderId);

  void approvePurchseOrder(Long vendorId, Long purchaseOrderId, ApproveDto approveDto);
}
