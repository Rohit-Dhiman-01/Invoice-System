package com.invoice.system.service.impl;

import com.invoice.system.config.exception.CustomerNotFoundException;
import com.invoice.system.config.exception.QuoteAlreadySentException;
import com.invoice.system.config.exception.QuoteNotFoundException;
import com.invoice.system.dto.ItemDto;
import com.invoice.system.dto.QuoteDto;
import com.invoice.system.model.*;
import com.invoice.system.repository.*;
import com.invoice.system.service.QuoteService;
import jakarta.transaction.Transactional;
import java.time.Year;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuoteServiceIMPL implements QuoteService {
  private final QuoteRepository quoteRepository;
  private final CustomerRepository customerRepository;
  private final QuoteSequenceNumberRepository sequenceNumberRepository;

  /**
   * Save the next sequence number for the future use or when application is restarted
   *
   * @return a string with format 'Q-2025-001'
   */
  @Transactional
  public String generateQuoteSequenceNumber() {
    int currentYear = Year.now().getValue();
    QuoteSequenceNumberEntity quoteSequenceNumber =
        sequenceNumberRepository
            .findById(currentYear)
            .orElse(new QuoteSequenceNumberEntity(currentYear));
    int newSequence = quoteSequenceNumber.getLastNumberUsed() + 1;
    quoteSequenceNumber.setLastNumberUsed(newSequence);

    sequenceNumberRepository.save(quoteSequenceNumber);
    return String.format("Q-%d:%03d", currentYear, newSequence);
  }

  /**
   * create Quote Entity with calculation the rate, tax, total for all the items
   *
   * @param quoteDto quote dto
   * @param customerId for which customer it has been creating
   * @return a Quote
   */
  @Override
  @Transactional
  public QuoteEntity createQuote(QuoteDto quoteDto, Long customerId) {
    CustomerEntity customer =
        customerRepository
            .findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));

    QuoteEntity quote = new QuoteEntity();
    quote.setQuoteNumber(generateQuoteSequenceNumber());
    quote.setQuoteDate(quoteDto.getQuoteDate());
    quote.setValidUntil(quoteDto.getValidUntil());
    quote.setStatus(QuoteStatus.DRAFT);
    quote.setCustomer(customer); // Associate with customer

    List<ItemEntity> itemEntities = new ArrayList<>();
    double totalBeforeTax = 0.0;
    double totalTaxAmount = 0.0;

    for (ItemDto itemDto : quoteDto.getItems()) {
      ItemEntity item = new ItemEntity();
      item.setItemName(itemDto.getItemName());
      item.setDescription(itemDto.getDescription());
      item.setQuantity(itemDto.getQuantity());
      item.setRate(itemDto.getRate());
      item.setHsnCode(itemDto.getHsnCode());
      item.setTaxPercent(itemDto.getTaxPercent());

      double itemTotal = (item.getRate() * item.getQuantity());
      double itemTax = (item.getTaxPercent() * itemTotal) / 100;

      item.setTotal(itemTotal + itemTax);
      item.setQuote(quote);

      totalBeforeTax += itemTotal;
      totalTaxAmount += itemTax;

      itemEntities.add(item);
    }

    quote.setSubTotal(totalBeforeTax + totalTaxAmount);
    quote.setTaxAmount(totalTaxAmount);
    quote.setItems(itemEntities);

    return quoteRepository.save(quote);
  }

  /**
   * if customer id is not present then, it retrieves all quote.
   *
   * @param customerId id for customer
   * @return all Quote for the given customer id
   */
  @Override
  public List<QuoteEntity> getAllQuote(Long customerId) {
    return quoteRepository.findAllQuotesByCustomerId(customerId);
  }

  /**
   * it will first check the customer if it is present or not, and then check for the quote
   *
   * @param quoteId id for quote
   * @param customerId id for customer
   * @return the quote for the customer
   */
  @Override
  public QuoteEntity getQuoteWithID(Long quoteId, Long customerId) {
    Optional<CustomerEntity> customerEntityOptional = customerRepository.findById(customerId);
    if (customerEntityOptional.isEmpty()) {
      throw new CustomerNotFoundException("Customer not found");
    }
    return quoteRepository
        .findAllQuote(quoteId, customerId)
        .orElseThrow(() -> new QuoteNotFoundException("Quote not found "));
  }

  /**
   * it will first check the customer if it is present or not, and then check for the quote
   *
   * @param quoteId id for quote
   * @param customerId id for customer
   * @param quoteDto new quote
   * @return the quote for the customer
   */
  @Override
  @Transactional
  public QuoteEntity updateQuoteWithID(Long quoteId, Long customerId, QuoteDto quoteDto) {
    CustomerEntity customer =
        customerRepository
            .findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));

    QuoteEntity existingQuote =
        quoteRepository
            .findByIdAndCustomerId(quoteId, customerId)
            .orElseThrow(() -> new QuoteNotFoundException("Quote not found for customer"));

    if (existingQuote.getStatus().equals(QuoteStatus.SENT)) {
      throw new QuoteAlreadySentException("Quote Already Sent");
    }

    existingQuote.setQuoteDate(quoteDto.getQuoteDate());
    existingQuote.setValidUntil(quoteDto.getValidUntil());

    List<ItemEntity> existingItems = existingQuote.getItems();
    existingItems.clear();

    double totalBeforeTax = 0.0;
    double totalTax = 0.0;

    for (ItemDto itemDto : quoteDto.getItems()) {
      ItemEntity item = new ItemEntity();
      item.setItemName(itemDto.getItemName());
      item.setDescription(itemDto.getDescription());
      item.setQuantity(itemDto.getQuantity());
      item.setRate(itemDto.getRate());
      item.setHsnCode(itemDto.getHsnCode());
      item.setTaxPercent(itemDto.getTaxPercent());

      double base = item.getRate() * item.getQuantity();
      double tax = (item.getTaxPercent() / 100) * base;
      item.setTotal(base + tax);

      item.setQuote(existingQuote); // maintain relationship
      existingItems.add(item); // ✅ add to existing list

      totalBeforeTax += base;
      totalTax += tax;
    }

    existingQuote.setSubTotal(totalBeforeTax + totalTax);
    existingQuote.setTaxAmount(totalTax);

    return quoteRepository.save(existingQuote);
  }

  /**
   * it will first check the customer if it is present or not, and then check for the quote
   *
   * @param quoteId id for quote
   * @param customerId id for customer
   */
  @Override
  @Transactional
  public void deleteQuoteWithID(Long quoteId, Long customerId) {
    if (!customerRepository.existsById(customerId)) {
      throw new CustomerNotFoundException("Customer not found");
    }
    if (quoteRepository.findByIdAndCustomerId(quoteId, customerId).isEmpty()) {
      throw new QuoteNotFoundException("Quote not found for customer");
    }
    quoteRepository.deleteById(quoteId);
  }
}
