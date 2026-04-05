package com.campus.share.service;

import com.campus.share.entity.Item;
import java.util.List;
import java.util.Optional;

public interface ItemService {
    Item create(Item item);
    Item update(Item item);
    void delete(Integer id);
    Optional<Item> findById(Integer id);
    List<Item> findAll();
    List<Item> findByCategory(Integer categoryId);
    List<Item> findByOwner(Integer ownerId);
    List<Item> findAvailableItems();
    List<Item> searchItems(String keyword);
    List<Item> findTopBorrowedItems(int limit);
}