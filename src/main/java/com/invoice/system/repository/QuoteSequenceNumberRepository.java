package com.invoice.system.repository;

import com.invoice.system.model.QuoteSequenceNumberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuoteSequenceNumberRepository extends JpaRepository<QuoteSequenceNumberEntity,Integer> {
}
