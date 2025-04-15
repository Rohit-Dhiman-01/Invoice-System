package com.invoice.system.service;

import com.invoice.system.dto.ItemDto;
import com.invoice.system.model.ItemEntity;
import java.util.List;

public interface ItemService {
  List<ItemEntity> getItemByNameIfNotThenCreateItem(List<ItemDto> itemDtoList);
}
