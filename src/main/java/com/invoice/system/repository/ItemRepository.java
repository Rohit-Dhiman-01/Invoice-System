package com.invoice.system.repository;

import com.invoice.system.model.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<ItemEntity,Long> {
    Optional<ItemEntity> findByItemNameIgnoreCase(String itemName);

}
