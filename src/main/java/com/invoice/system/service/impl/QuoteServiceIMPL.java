package com.invoice.system.service.impl;

import com.invoice.system.config.exception.CustomerNotFoundException;
import com.invoice.system.config.exception.QuoteNotFoundException;
import com.invoice.system.dto.ItemDto;
import com.invoice.system.dto.QuoteDto;
import com.invoice.system.model.*;
import com.invoice.system.repository.*;
import com.invoice.system.service.ItemService;
import com.invoice.system.service.QuoteService;
import jakarta.transaction.Transactional;
import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuoteServiceIMPL implements QuoteService {
  private final QuoteRepository quoteRepository;

  private final CustomerRepository customerRepository;
  private final ItemService itemService;
  // repository for number generation for quote number
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
  public QuoteEntity createQuote(QuoteDto quoteDto, Long customerId) {
    if (!customerRepository.existsById(customerId)) {
      throw new CustomerNotFoundException("Customer Not found");
    }
    QuoteEntity entity = new QuoteEntity();
    entity.setQuoteNumber(generateQuoteSequenceNumber());
    // calculating total without tax, tax amount through tax percentage, total amount with tax
    // amount
    double totalBeforeTax = 0.0, sumOfAllTaxes = 0.0;
    for (ItemDto item : quoteDto.getItems()) {
      totalBeforeTax += (item.getRate() * item.getQuantity());
      sumOfAllTaxes += ((item.getTaxPercent() * item.getRate()) / 100) * item.getQuantity();
    }
    entity.setSubTotal(totalBeforeTax);
    entity.setTextAmount(sumOfAllTaxes);
    entity.setSubTotal(totalBeforeTax + sumOfAllTaxes);
    // finding customer through customer id
    Optional<CustomerEntity> customerEntityOptional = customerRepository.findById(customerId);
    if (customerEntityOptional.isEmpty()) {
      throw new CustomerNotFoundException("Customer not found ");
    }
    entity.setCustomer(customerEntityOptional.get());

    entity.setQuoteDate(quoteDto.getQuoteDate());
    entity.setValidUntil(quoteDto.getValidUntil());
    entity.setStatus(QuoteStatus.DRAFT);

    // getting item by there name if not present then creating that items
    List<ItemEntity> items = itemService.getItemByNameIfNotThenCreateItem(quoteDto.getItems());
    for (ItemEntity item : items) {
      item.setQuote(entity);
    }
    entity.setItems(items);
    return quoteRepository.save(entity);
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

  @Override
  @Transactional
  public QuoteEntity updateQuoteWithID(Long quoteId, Long customerId, QuoteDto quoteDto) {

    if (!customerRepository.existsById(customerId))
      throw new CustomerNotFoundException("customer not found");
    if (!quoteRepository.existsByIdAndCustomerId(quoteId, customerId)) {
      throw new QuoteNotFoundException("Quote not found for customer");
    }

    QuoteEntity existingQuote =
        quoteRepository
            .findById(quoteId)
            .orElseThrow(() -> new QuoteNotFoundException("Quote not found"));

    existingQuote.setQuoteDate(quoteDto.getQuoteDate());
    existingQuote.setValidUntil(quoteDto.getValidUntil());

    existingQuote.getItems().clear();

    // Map to merge items with the same name
    Map<String, ItemEntity> mergedItems = new HashMap<>();

    double totalBeforeTax = 0.0;
    double totalTax = 0.0;

    for (ItemDto itemDto : quoteDto.getItems()) {
      String key = itemDto.getItemName(); // Can also use HSN if preferred: itemDto.getHsnCode()

      if (mergedItems.containsKey(key)) {
        ItemEntity existing = mergedItems.get(key);
        int newQuantity = existing.getQuantity() + itemDto.getQuantity();
        existing.setQuantity(newQuantity);

        double base = existing.getRate() * newQuantity;
        double tax = (existing.getTaxPercent() / 100) * base;
        existing.setTotal(base + tax);

      } else {
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

        item.setQuote(existingQuote);

        mergedItems.put(key, item);
      }
    }

    for (ItemEntity item : mergedItems.values()) {
      existingQuote.getItems().add(item);
      totalBeforeTax += item.getRate() * item.getQuantity();
      totalTax += (item.getTaxPercent() / 100) * item.getRate() * item.getQuantity();
    }

    existingQuote.setSubTotal(totalBeforeTax + totalTax);
    existingQuote.setTextAmount(totalTax);

    return quoteRepository.save(existingQuote);
  }

  @Override
  @Transactional
  public void deleteQuoteWithID(Long quoteId, Long customerId) {
    if (!customerRepository.existsById(customerId)) {
      throw new CustomerNotFoundException("Customer not found");
    }
    if (!quoteRepository.existsByIdAndCustomerId(quoteId, customerId)) {
      throw new QuoteNotFoundException("Quote not found for customer");
    }
    quoteRepository.deleteById(quoteId);
  }
}
