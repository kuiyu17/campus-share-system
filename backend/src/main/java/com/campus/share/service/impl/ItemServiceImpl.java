package com.campus.share.service.impl;

import com.campus.share.entity.Item;
import com.campus.share.entity.BorrowRecord;
import com.campus.share.repository.ItemRepository;
import com.campus.share.repository.BorrowRecordRepository;
import com.campus.share.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ItemServiceImpl implements ItemService {
    
    @Autowired
    private ItemRepository itemRepository;
    
    @Override
    public Item create(Item item) {
        return itemRepository.save(item);
    }
    
    @Override
    public Item update(Item item) {
        return itemRepository.save(item);
    }
    
    @Autowired
    private BorrowRecordRepository borrowRecordRepository;
    
    @Override
    public void delete(Integer id) {
        // 先删除与该物品相关的所有借用记录
        List<BorrowRecord> records = borrowRecordRepository.findByItemId(id);
        for (BorrowRecord record : records) {
            borrowRecordRepository.delete(record);
        }
        // 然后删除物品
        itemRepository.deleteById(id);
    }
    
    @Override
    public Optional<Item> findById(Integer id) {
        return itemRepository.findById(id);
    }
    
    @Override
    public List<Item> findAll() {
        return itemRepository.findAll();
    }
    
    @Override
    public List<Item> findByCategory(Integer categoryId) {
        return itemRepository.findByCategoryId(categoryId);
    }
    
    @Override
    public List<Item> findByOwner(Integer ownerId) {
        return itemRepository.findByOwnerId(ownerId);
    }
    
    @Override
    public List<Item> findAvailableItems() {
        return itemRepository.findByStatus(Item.Status.available);
    }
    
    @Override
    public List<Item> searchItems(String keyword) {
        return itemRepository.searchItems(Item.Status.available, keyword);
    }
    
    @Override
    public List<Item> findTopBorrowedItems(int limit) {
        return itemRepository.findTopBorrowedItems(Item.Status.available, PageRequest.of(0, limit));
    }
}