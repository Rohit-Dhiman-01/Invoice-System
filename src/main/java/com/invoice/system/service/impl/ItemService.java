package com.invoice.system.service.impl;

import com.invoice.system.dto.ItemDto;
import com.invoice.system.model.ItemEntity;
import com.invoice.system.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;

    /**
     * It will get the item entity and if it is not present then create item
     *
     * @param itemDtoList a list of item dto
     * @return a list of ids of items.
     */
    public List<Long> getItemByNameIfNotThenCreateItem(List<ItemDto> itemDtoList) {
        List<Long> answer = new ArrayList<>();
        itemDtoList.forEach(itemDto -> {
            Optional<ItemEntity> itemEntityOptional = itemRepository.findByItemNameIgnoreCase(itemDto.getItemName());
            if (itemEntityOptional.isEmpty()) {
                ItemEntity item = getItem(itemDto);

                ItemEntity saveItemEntity = itemRepository.save(item);
                answer.add(saveItemEntity.getId());
            } else {
                ItemEntity item = itemEntityOptional.get();
                answer.add(item.getId());
            }
        });
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
        double RateAndTaxPercentage = ((itemDto.getTaxPercent() * itemDto.getRate()) / 100);
        item.setTotal(RateAndTaxPercentage * itemDto.getQuantity());
        return item;
    }
}
