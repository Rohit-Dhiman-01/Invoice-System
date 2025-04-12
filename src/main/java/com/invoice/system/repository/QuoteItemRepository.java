package com.invoice.system.repository;

import com.invoice.system.model.QuoteItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuoteItemRepository extends JpaRepository<QuoteItemEntity,Integer> {

}
