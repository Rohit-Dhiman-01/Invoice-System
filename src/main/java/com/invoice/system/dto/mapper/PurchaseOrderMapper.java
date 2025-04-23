package com.invoice.system.dto.mapper;

import com.invoice.system.dto.PurchaseOrderResponse;
import com.invoice.system.model.PurchaseOrderEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PurchaseOrderMapper {
  @Mapping(source = "quote.items", target = "items")
  @Mapping(source = "quote.currency", target = "currency")
  PurchaseOrderResponse toPurchaseOrderResponse(PurchaseOrderEntity entity);

  @Mapping(source = "quote.currency", target = "currency")
  @Mapping(source = "quote.items", target = "items")
  List<PurchaseOrderResponse> toPurchaseOrderResponseList(List<PurchaseOrderEntity> entity);
}
