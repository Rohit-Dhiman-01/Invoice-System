package com.invoice.system.repository;

import com.invoice.system.model.InvoiceEntitySequenceNumberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceSequenceNumberRepository extends JpaRepository<InvoiceEntitySequenceNumberEntity, Integer> {
}
