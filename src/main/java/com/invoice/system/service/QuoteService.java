package com.invoice.system.service;


import com.invoice.system.dto.QuoteDto;
import com.invoice.system.model.QuoteEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface QuoteService {
    QuoteEntity createVendor(QuoteDto quoteDto, Long customer_id);
    List<QuoteEntity> getAllQuote();
    QuoteEntity getQuoteWithID(Long quoteId);
}
