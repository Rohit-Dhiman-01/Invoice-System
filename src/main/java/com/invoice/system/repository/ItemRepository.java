package com.invoice.system.repository;

import com.invoice.system.model.ItemEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<ItemEntity, Long> {
  Optional<ItemEntity> findByItemNameIgnoreCase(String itemName);
}
