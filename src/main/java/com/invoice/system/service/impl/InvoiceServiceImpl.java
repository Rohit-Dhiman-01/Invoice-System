package com.invoice.system.service.impl;

import com.invoice.system.config.exception.CustomerNotFoundException;
import com.invoice.system.config.exception.InvoiceNotFoundException;
import com.invoice.system.config.exception.PurchaseOrderNotFoundException;
import com.invoice.system.config.exception.QuoteNotFoundException;
import com.invoice.system.dto.ApprovePaymentDto;
import com.invoice.system.dto.InvoiceDto;
import com.invoice.system.dto.InvoiceResponse;
import com.invoice.system.dto.mapper.InvoiceMapper;
import com.invoice.system.model.*;
import com.invoice.system.repository.CustomerRepository;
import com.invoice.system.repository.InvoiceRepository;
import com.invoice.system.repository.InvoiceSequenceNumberRepository;
import com.invoice.system.repository.PurchaseOrderRepository;
import com.invoice.system.service.InvoiceService;
import jakarta.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.time.Year;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvoiceServiceImpl implements InvoiceService {

  @Autowired private CustomerRepository customerRepository;
  @Autowired private PurchaseOrderRepository purchaseOrderRepository;
  @Autowired private InvoiceRepository invoiceEntityRepository;
  @Autowired private InvoiceSequenceNumberRepository invoiceSequenceNumberRepository;
  @Autowired private InvoiceMapper invoiceMapper;

  @Transactional
  public String generateInvoiceSequenceNumber() {
    int currentYear = Year.now().getValue();
    InvoiceEntitySequenceNumberEntity invoiceSequenceNumber =
        invoiceSequenceNumberRepository
            .findById(currentYear)
            .orElse(new InvoiceEntitySequenceNumberEntity(currentYear));

    int newSequence = invoiceSequenceNumber.getLastNumberUsed() + 1;
    invoiceSequenceNumber.setLastNumberUsed(newSequence);

    invoiceSequenceNumberRepository.save(invoiceSequenceNumber);
    return String.format("INV-%d-%03d", currentYear, newSequence);
  }

  @Override
  public InvoiceResponse createInvoice(
      Long customerId, Long purchaseOrderId, InvoiceDto invoiceDto) {

    CustomerEntity customer =
        customerRepository
            .findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
    PurchaseOrderEntity purchaseOrder =
        purchaseOrderRepository
            .findById(purchaseOrderId)
            .orElseThrow(() -> new PurchaseOrderNotFoundException("Purchase Order Not Found"));
    if (!purchaseOrder.getQuote().getCustomer().getId().equals(customerId)) {
      throw new InvoiceNotFoundException("Invoice not found for this customer");
    }
    if (!purchaseOrder.getStatus().equals(PurchaseOrderStatus.APPROVED)) {
      throw new QuoteNotFoundException("Purchase Order Not Approved");
    }
    InvoiceEntity invoiceEntity = new InvoiceEntity();
    invoiceEntity.setId(null);
    invoiceEntity.setInvoiceNumber(generateInvoiceSequenceNumber());
    invoiceEntity.setInvoiceDate(invoiceDto.getInvoiceDate());
    invoiceEntity.setDueDate(invoiceDto.getDueDate());
    invoiceEntity.setPaymentStatus(InvoiceEntityPaymentStatus.UNPAID);
    invoiceEntity.setSubTotal(purchaseOrder.getSubTotal());
    invoiceEntity.setTaxAmount(purchaseOrder.getTaxAmount());
    invoiceEntity.setTotalAmount(purchaseOrder.getTotalAmount());
    invoiceEntity.setDueAmount(0.0);
    invoiceEntity.setCustomer(customer);
    //    invoiceEntity.setDueAmount(purchaseOrder.getTotalAmount());
    invoiceEntity.setPurchaseOrder(purchaseOrder);

    return invoiceMapper.toInvoiceResponse(invoiceEntityRepository.save(invoiceEntity));
  }

  @Override
  public List<InvoiceResponse> getAllInvoice(Long customerId) {
    CustomerEntity customer =
        customerRepository
            .findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException("Customer Not Found"));

    return invoiceMapper.toInvoiceResponseList(
        invoiceEntityRepository.findByCustomerId(customerId));
  }

  @Override
  public InvoiceResponse getInvoiceById(Long customerId, Long invoiceId) {
    CustomerEntity customer =
        customerRepository
            .findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException("Customer Not Found"));
    return invoiceMapper.toInvoiceResponse(
        invoiceEntityRepository
            .findByCustomerIdAndId(customerId, invoiceId)
            .orElseThrow(
                () -> new InvoiceNotFoundException("Invoice Not found for this Customer")));
  }

  @Override
  public ByteArrayInputStream generateInvoicePdf(Long customerId, Long invoiceId) {
    InvoiceEntity invoiceEntity =
        invoiceEntityRepository
            .findByCustomerIdAndId(customerId, invoiceId)
            .orElseThrow(() -> new InvoiceNotFoundException("Invoice Not found for this Customer"));
    return new InvoicePdfGeneration().generateInvoicePdf(invoiceEntity);
  }

  @Override
  public void approveInvoice(Long invoiceId, Long customerId, ApprovePaymentDto approvePaymentDto) {
    CustomerEntity customer =
        customerRepository
            .findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException("Customer Not Found"));
    InvoiceEntity invoice =
        invoiceEntityRepository
            .findByCustomerIdAndId(customerId, invoiceId)
            .orElseThrow(() -> new InvoiceNotFoundException("Invoice Not found for this Customer"));
    if (Objects.equals(invoice.getTotalAmount(), approvePaymentDto.getAmountPaid())) {
      invoice.setDueAmount(0.0);
      invoice.setPaymentStatus(InvoiceEntityPaymentStatus.PAID);
    }
    if (invoice.getTotalAmount() > approvePaymentDto.getAmountPaid()) {
      invoice.setDueAmount(invoice.getTotalAmount() - approvePaymentDto.getAmountPaid());
      invoice.setPaymentStatus(InvoiceEntityPaymentStatus.PARTIALLY_PAID);
    }

    invoiceEntityRepository.save(invoice);
  }
}
