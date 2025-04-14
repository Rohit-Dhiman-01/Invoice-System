package com.invoice.system.service.impl;

import com.invoice.system.dto.ItemDto;
import com.invoice.system.model.ItemEntity;
import com.invoice.system.repository.ItemRepository;
import com.invoice.system.service.ItemService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemServiceImpl implements ItemService {
  @Autowired private ItemRepository itemRepository;

  /**
   * It will get the item entity and if it is not present then create item
   *
   * @param itemDtoList a list of item dto
   * @return a list of ids of items.
   */
  @Override
  public List<ItemEntity> getItemByNameIfNotThenCreateItem(List<ItemDto> itemDtoList) {

    Map<String, ItemEntity> itemMap = new HashMap<>();
    List<ItemEntity> answer = new ArrayList<>();

    for (ItemDto itemDto : itemDtoList) {
      String nameKey = itemDto.getItemName().trim().toLowerCase();

      ItemEntity item = itemMap.get(nameKey);
      if (item != null) {
        item.setQuantity(item.getQuantity() + itemDto.getQuantity());
      } else {
        item = getItem(itemDto);
        itemMap.put(nameKey, item);
      }
    }

    // Save all unique or updated items
    for (ItemEntity item : itemMap.values()) {
      // Optionally recalculate total here based on updated quantity
      double baseAmount = item.getRate() * item.getQuantity();
      double taxAmount = (item.getTaxPercent() / 100) * baseAmount;
      item.setTotal(baseAmount + taxAmount);

      ItemEntity saved = itemRepository.save(item);
      answer.add(saved);
    }

    return answer;
  }

  private static ItemEntity getItem(ItemDto itemDto) {
    ItemEntity item = new ItemEntity();
    item.setId(null);
    item.setItemName(itemDto.getItemName());
    item.setDescription(itemDto.getDescription());
    item.setDescription(itemDto.getDescription());
    item.setQuantity(itemDto.getQuantity());
    item.setRate(itemDto.getRate());
    item.setHsnCode(itemDto.getHsnCode());
    item.setTaxPercent(itemDto.getTaxPercent());
    double baseAmount = itemDto.getRate() * itemDto.getQuantity();
    double taxAmount = (itemDto.getTaxPercent() / 100) * baseAmount;
    item.setTotal(baseAmount + taxAmount);
    return item;
  }
}
