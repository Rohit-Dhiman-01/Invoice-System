package com.invoice.system.dto.mapper;

import com.invoice.system.dto.QuoteResponse;
import com.invoice.system.model.QuoteEntity;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuoteMapper {
  QuoteResponse toQuoteResponse(QuoteEntity quoteEntity);

  List<QuoteResponse> toQuoteResponse(List<QuoteEntity> quoteEntity);
}
