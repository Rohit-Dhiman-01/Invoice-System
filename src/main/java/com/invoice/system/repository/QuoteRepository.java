package com.invoice.system.repository;

import com.invoice.system.model.QuoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuoteRepository extends JpaRepository<QuoteEntity,Long> {
    @Query("SELECT DISTINCT q FROM QuoteEntity q JOIN FETCH q.items")
    List<QuoteEntity> findAllQuote();

    @Query("SELECT DISTINCT q FROM QuoteEntity q JOIN FETCH q.items WHERE q.id = :id")
    Optional<QuoteEntity> findAllQuote(@Param("id")Long Id);
}
