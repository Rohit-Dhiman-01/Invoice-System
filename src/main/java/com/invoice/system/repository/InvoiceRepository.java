package com.invoice.system.repository;

import com.invoice.system.model.InvoiceEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<InvoiceEntity, Long> {
  List<InvoiceEntity> findByCustomerId(Long customerId);

  Optional<InvoiceEntity> findByCustomerIdAndId(Long customerId, Long invoiceId);

  Optional<InvoiceEntity> findByInvoiceNumberIgnoreCase(String invoiceNumber);
}
