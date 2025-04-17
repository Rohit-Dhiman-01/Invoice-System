package com.invoice.system.repository;

import com.invoice.system.model.PurchaseOrderSequenceNumberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderSequenceNumberRepository
    extends JpaRepository<PurchaseOrderSequenceNumberEntity, Integer> {}
