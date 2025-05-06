package com.invoice.system.repository;

import com.invoice.system.model.PurchaseOrderEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrderEntity, Long> {
  Optional<PurchaseOrderEntity> findByPoNumberIgnoreCase(String poNumber);
}
