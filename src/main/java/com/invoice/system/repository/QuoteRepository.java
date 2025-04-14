package com.invoice.system.repository;

import com.invoice.system.model.QuoteEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuoteRepository extends JpaRepository<QuoteEntity, Long> {

  List<QuoteEntity> findAllQuotesByCustomerId(@Param("customerId") Long customerId);

  @Query(
      "SELECT DISTINCT q FROM QuoteEntity q JOIN FETCH q.items WHERE q.id = :quoteId AND q.customer.id = :customerId")
  Optional<QuoteEntity> findAllQuote(
      @Param("quoteId") Long Id, @Param("customerId") Long customerId);

  boolean existsByIdAndCustomerId(Long quoteId, Long customerId);
}
