package com.invoice.system.dto.mapper;

import com.invoice.system.dto.QuoteDto;
import com.invoice.system.model.QuoteEntity;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface QuoteMapper {
    QuoteEntity toItemEntity(QuoteDto quoteDto);

    QuoteDto toItemDto(QuoteEntity quoteEntity);
}
