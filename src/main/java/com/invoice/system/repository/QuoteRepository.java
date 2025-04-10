package com.invoice.system.repository;

import com.invoice.system.model.QuoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuoteRepository extends JpaRepository<QuoteEntity,Long> {
}
