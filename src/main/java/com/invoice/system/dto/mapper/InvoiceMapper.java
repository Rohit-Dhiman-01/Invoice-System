package com.invoice.system.dto.mapper;

import com.invoice.system.dto.InvoiceResponse;
import com.invoice.system.model.InvoiceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {
    @Mapping(source = "purchaseOrder.quote.items", target = "items")
    List<InvoiceResponse> toInvoiceResponseList(List<InvoiceEntity> invoiceEntity);

    @Mapping(source = "purchaseOrder.quote.items", target = "items")
    InvoiceResponse toInvoiceResponse(InvoiceEntity invoiceEntity);
}