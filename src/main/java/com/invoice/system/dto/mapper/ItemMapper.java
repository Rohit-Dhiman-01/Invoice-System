package com.invoice.system.dto.mapper;

import com.invoice.system.dto.ItemDto;
import com.invoice.system.model.ItemEntity;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ItemMapper {
  ItemEntity toItemEntity(ItemDto itemDto);

  List<ItemEntity> toItemList(List<ItemDto> itemDto);

  List<ItemDto> toItemDtoList(List<ItemEntity> items);
}
