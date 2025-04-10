package com.invoice.system.dto.mapper;

import com.invoice.system.dto.CustomerDto;
import com.invoice.system.dto.ItemDto;
import com.invoice.system.model.CustomerEntity;
import com.invoice.system.model.ItemEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    ItemEntity toItemEntity(ItemDto itemDto);

    ItemDto toItemDto(ItemEntity itemEntity);

    List<ItemDto> toItemDtoList(List<ItemEntity> items);
}
