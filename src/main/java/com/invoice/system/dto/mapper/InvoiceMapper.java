package com.invoice.system.dto.mapper;

import com.invoice.system.dto.InvoiceResponse;
import com.invoice.system.model.InvoiceEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {
  @Mapping(source = "purchaseOrder.quote.items", target = "items")
  @Mapping(source = "purchaseOrder.quote.currency", target = "currency")
  List<InvoiceResponse> toInvoiceResponseList(List<InvoiceEntity> invoiceEntity);

  @Mapping(source = "purchaseOrder.quote.items", target = "items")
  @Mapping(source = "purchaseOrder.quote.currency", target = "currency")
  InvoiceResponse toInvoiceResponse(InvoiceEntity invoiceEntity);
}
