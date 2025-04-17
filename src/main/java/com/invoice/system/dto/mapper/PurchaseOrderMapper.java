package com.invoice.system.dto.mapper;

import com.invoice.system.dto.PurchaseOrderDto;
import com.invoice.system.model.PurchaseOrderEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PurchaseOrderMapper {
  PurchaseOrderEntity toPurchaseOrderEntity(PurchaseOrderDto purchaseOrderDto);

  PurchaseOrderDto toPurchaseOrderDto(PurchaseOrderEntity purchaseOrderEntity);
}
