package com.invoice.system.service.impl;

import com.invoice.system.config.exception.CustomerNotFoundException;
import com.invoice.system.config.exception.model.QuoteNotFoundException;
import com.invoice.system.dto.ItemDto;
import com.invoice.system.dto.QuoteDto;
import com.invoice.system.model.*;
import com.invoice.system.repository.*;
import com.invoice.system.service.QuoteService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.List;
import java.util.Optional;
@Service
public class QuoteServiceIMPL implements QuoteService {
    @Autowired
    private QuoteRepository quoteRepository;
    @Autowired
    private QuoteItemRepository quoteItemRepository;
    @Autowired
    public CustomerRepository customerRepository;
    @Autowired
    private ItemService itemService;
    @Autowired
    private ItemRepository itemRepository;
// repository for number generation for quote number
    @Autowired
    private QuoteSequenceNumberRepository sequenceNumberRepository;

    @Transactional
    public String generateQuoteSequenceNumber(){
        int currentYear = Year.now().getValue();
        QuoteSequenceNumberEntity quoteSequenceNumber = sequenceNumberRepository.findById(currentYear)
                .orElse(new QuoteSequenceNumberEntity(currentYear));
        int newSequence = quoteSequenceNumber.getLastNumberUsed() + 1;
        quoteSequenceNumber.setLastNumberUsed(newSequence);

        sequenceNumberRepository.save(quoteSequenceNumber);
        return  String.format("Q-%d:%03d",currentYear,newSequence);
    }

    @Override
    public QuoteEntity createVendor(QuoteDto quoteDto, Long customerId) {
        QuoteEntity entity =new QuoteEntity();
        entity.setQuoteNumber(generateQuoteSequenceNumber());
// calculating total without tax, tax amount through tax percentage, total amount with tax amount
        double totalBeforeTax =0.0, sumOfAllTaxes =0.0;
        for(ItemDto item:quoteDto.getItems()){
            totalBeforeTax +=(item.getRate() * item.getQuantity());
            sumOfAllTaxes +=((item.getTaxPercent() * item.getRate())/100)* item.getQuantity();
        }
        entity.setSubTotal(totalBeforeTax);
        entity.setTextAmount(sumOfAllTaxes);
        entity.setSubTotal(totalBeforeTax+ sumOfAllTaxes);
// finding customer through customer id
        Optional<CustomerEntity> customerEntityOptional= customerRepository.findById(customerId);
        if(customerEntityOptional.isEmpty()){
            throw new CustomerNotFoundException("Customer not found for id: "+ customerId);
        }
        entity.setCustomer(customerEntityOptional.get());

        entity.setQuoteDate(quoteDto.getQuoteDate());
        entity.setValidUntil(quoteDto.getValidUntil());
        entity.setStatus(QuoteStatus.DRAFT);
// saving quote into database
        QuoteEntity quoteEntity = quoteRepository.save(entity);
// getting item by there name if not present then creating that items
        List<Long> itemIds = itemService.getItemByNameIfNotThenCreateItem(quoteDto.getItems());
        itemIds.forEach(itemId->{
            QuoteItemEntity quoteItemEntity = new QuoteItemEntity();
            quoteItemEntity.setQuoteId(quoteEntity);
            quoteItemEntity.setItemId(itemRepository.findById(itemId).orElseThrow());
            quoteItemRepository.save(quoteItemEntity);
        });
        return quoteEntity;
    }

    @Override
    public List<QuoteEntity> getAllQuote() {
        return quoteRepository.findAllQuote();
    }

    @Override
    public QuoteEntity getQuoteWithID(Long quoteId) {
        return quoteRepository.findAllQuote(quoteId)
                .orElseThrow(()-> new QuoteNotFoundException("Quote not found with id:- "+quoteId));
    }


}
