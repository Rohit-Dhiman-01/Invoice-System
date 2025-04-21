package com.invoice.system.repository;

import com.invoice.system.model.InvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<InvoiceEntity, Long> {
    List<InvoiceEntity> findByCustomerId(Long customerId);

    Optional<InvoiceEntity> findByCustomerIdAndId(Long customerId, Long invoiceId);
}