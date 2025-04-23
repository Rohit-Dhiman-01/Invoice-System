package com.invoice.system.service.impl;

import com.invoice.system.config.exception.PurchaseOrderNotFoundException;
import com.invoice.system.config.exception.QuoteAlreadySentException;
import com.invoice.system.config.exception.QuoteNotFoundException;
import com.invoice.system.config.exception.VendorNotFoundException;
import com.invoice.system.dto.PurchaseOrderDto;
import com.invoice.system.dto.PurchaseOrderResponse;
import com.invoice.system.dto.mapper.PurchaseOrderMapper;
import com.invoice.system.model.*;
import com.invoice.system.repository.*;
import com.invoice.system.service.PurchaseOrderService;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import jakarta.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.time.Year;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PurchaseOrderServiceImpl implements PurchaseOrderService {
  private final VendorRepository vendorRepository;
  private final QuoteRepository quoteRepository;
  private final PurchaseOrderSequenceNumberRepository purchaseOrderSequenceNumberRepository;
  private final PurchaseOrderRepository purchaseOrderRepository;
  private final PurchaseOrderMapper purchaseOrderMapper;
  private final POPdfGeneration pdfGeneration;

  @Transactional
  public String generateQuoteSequenceNumber() {
    int currentYear = Year.now().getValue();
    PurchaseOrderSequenceNumberEntity quoteSequenceNumber =
        purchaseOrderSequenceNumberRepository
            .findById(currentYear)
            .orElse(new PurchaseOrderSequenceNumberEntity(currentYear));
    int newSequence = quoteSequenceNumber.getLastNumberUsed() + 1;
    quoteSequenceNumber.setLastNumberUsed(newSequence);

    purchaseOrderSequenceNumberRepository.save(quoteSequenceNumber);
    return String.format("PO-%d-%03d", currentYear, newSequence);
  }

  @Override
  @Transactional
  public PurchaseOrderResponse createPurchaseOrder(
      Long vendorId, Long quoteId, PurchaseOrderDto purchaseOrderDto) {
    VendorEntity vendor =
        vendorRepository
            .findById(vendorId)
            .orElseThrow(() -> new VendorNotFoundException("Vendor Not Found"));

    QuoteEntity quote =
        quoteRepository
            .findById(quoteId)
            .orElseThrow(() -> new QuoteNotFoundException("Quote Not Found"));

    PurchaseOrderEntity purchaseOrder = new PurchaseOrderEntity();
    purchaseOrder.setPoNumber(generateQuoteSequenceNumber());
    purchaseOrder.setPoDate(purchaseOrderDto.getPoDate());
    purchaseOrder.setShippingAddress(quote.getCustomer().getShippingAddress());
    purchaseOrder.setQuote(quote);
    purchaseOrder.setVendor(vendor);
    purchaseOrder.setSubTotal(quote.getSubTotal());
    purchaseOrder.setTaxAmount(quote.getTaxAmount());
    purchaseOrder.setTotalAmount(quote.getTotalAmount());
    if (!quote.getStatus().equals(QuoteStatus.DRAFT)) {
      throw new QuoteAlreadySentException("Quote Already sent");
    }
    quote.setStatus(QuoteStatus.SENT);
    quoteRepository.save(quote);
    purchaseOrder.setStatus(PurchaseOrderStatus.DRAFT);
    return purchaseOrderMapper.toPurchaseOrderResponse(purchaseOrderRepository.save(purchaseOrder));
  }

  @Override
  public List<PurchaseOrderResponse> getAllPurchaseOrders(Long vendorId) {
    VendorEntity vendor =
        vendorRepository
            .findById(vendorId)
            .orElseThrow(() -> new VendorNotFoundException("Vendor Not Found"));
    return purchaseOrderMapper.toPurchaseOrderResponseList(vendor.getPurchaseOrders());
  }

  @Override
  public PurchaseOrderResponse getPurchaseOrderById(Long vendorId, Long purchaseOrderId) {
    VendorEntity vendor =
        vendorRepository
            .findById(vendorId)
            .orElseThrow(() -> new VendorNotFoundException("Vendor Not Found"));
    PurchaseOrderEntity purchaseOrder =
        vendor.getPurchaseOrders().stream()
            .filter(po -> po.getId().equals(purchaseOrderId))
            .findFirst()
            .orElseThrow(
                () ->
                    new PurchaseOrderNotFoundException("Purchase Order Not Found for this vendor"));

    return purchaseOrderMapper.toPurchaseOrderResponse(purchaseOrder);
  }

  @Override
  public byte[] generatePurchaseOrderPdf(Long vendorId, Long purchaseOrderId) {
    PurchaseOrderResponse purchaseOrder = getPurchaseOrderById(vendorId, purchaseOrderId);
    ByteArrayOutputStream out = new ByteArrayOutputStream();

    try {
      PdfWriter memoryWriter = new PdfWriter(out);
      PdfDocument pdfDocMemory = new PdfDocument(memoryWriter);
      Document document = new Document(pdfDocMemory, PageSize.A4);
      document.setMargins(30, 30, 30, 30);

      pdfGeneration.generatePdfContent(document, purchaseOrder);
      document.close();

      pdfGeneration.createPdfOnDisk(purchaseOrder);

    } catch (Exception e) {
      e.printStackTrace();
    }

    return out.toByteArray();
  }
}
