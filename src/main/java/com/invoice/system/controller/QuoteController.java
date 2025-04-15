package com.invoice.system.controller;

import com.invoice.system.dto.QuoteDto;
import com.invoice.system.model.QuoteEntity;
import com.invoice.system.service.impl.QuoteServiceIMPL;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer/{customerId}/quotes")
public class QuoteController {
  @Autowired private QuoteServiceIMPL quoteServiceIMPL;

  @PostMapping
  public ResponseEntity<QuoteEntity> createQuote(
      @RequestBody QuoteDto quoteDto, @PathVariable Long customerId) {
    return new ResponseEntity<>(
        quoteServiceIMPL.createQuote(quoteDto, customerId), HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<QuoteEntity>> getAllQuote(
      @PathVariable(value = "customerId", required = false) Long customerId) {
    return new ResponseEntity<>(quoteServiceIMPL.getAllQuote(customerId), HttpStatus.OK);
  }

  @GetMapping("{quoteId}")
  public ResponseEntity<QuoteEntity> getQuoteWithID(
      @PathVariable Long quoteId, @PathVariable Long customerId) {
    return new ResponseEntity<>(
        quoteServiceIMPL.getQuoteWithID(quoteId, customerId), HttpStatus.OK);
  }

  @PutMapping("{quoteId}")
  public ResponseEntity<QuoteEntity> updateQuoteWithID(
      @PathVariable Long quoteId, @PathVariable Long customerId, @RequestBody QuoteDto quoteDto) {
    return new ResponseEntity<>(
        quoteServiceIMPL.updateQuoteWithID(quoteId, customerId, quoteDto), HttpStatus.OK);
  }

  @DeleteMapping("{quoteId}")
  public ResponseEntity<Void> deleteQuoteWithID(
      @PathVariable Long quoteId, @PathVariable Long customerId) {
    quoteServiceIMPL.deleteQuoteWithID(quoteId, customerId);
    return ResponseEntity.status(HttpStatus.OK).build();
  }
}
