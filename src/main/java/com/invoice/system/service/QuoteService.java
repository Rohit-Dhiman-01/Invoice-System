package com.invoice.system.service;

import com.invoice.system.dto.ApproveDto;
import com.invoice.system.dto.QuoteDto;
import com.invoice.system.dto.QuoteResponse;
import java.io.ByteArrayInputStream;
import java.util.List;

public interface QuoteService {
  QuoteResponse createQuote(QuoteDto quoteDto, Long customer_id);

  List<QuoteResponse> getAllQuote(Long customerId);

  QuoteResponse getQuoteWithID(Long quoteId, Long customerId);

  QuoteResponse updateQuoteWithID(Long quoteId, Long customerId, QuoteDto quoteDto);

  void deleteQuoteWithID(Long quoteId, Long customerId);

  void approveQuote(Long quoteId, Long customerId, ApproveDto approveDto);

  ByteArrayInputStream generateQuotePdf(Long quoteID, Long customerId);
}
