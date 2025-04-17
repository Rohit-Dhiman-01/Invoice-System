package com.invoice.system.dto.mapper;

import com.invoice.system.dto.QuoteDto;
import com.invoice.system.model.QuoteEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuoteMapper {
  QuoteEntity toQuoteEntity(QuoteDto quoteDto);

  QuoteDto toQuoteDto(QuoteEntity quoteEntity);
}
