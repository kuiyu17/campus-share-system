package com.campus.share.controller;

import com.campus.share.entity.Item;
import com.campus.share.entity.User;
import com.campus.share.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/items")
public class ItemController {
    
    @Autowired
    private ItemService itemService;
    
    @PostMapping("/create")
    public ResponseEntity<Item> create(@RequestBody Item item) {
        // 处理ownerId参数
        if (item.getOwner() != null && item.getOwner().getId() != null) {
            User owner = new User();
            owner.setId(item.getOwner().getId());
            item.setOwner(owner);
        }
        Item createdItem = itemService.create(item);
        return new ResponseEntity<>(createdItem, HttpStatus.CREATED);
    }
    
    @PutMapping("/update")
    public ResponseEntity<Item> update(@RequestBody Item item) {
        // 处理ownerId参数
        if (item.getOwner() != null && item.getOwner().getId() != null) {
            User owner = new User();
            owner.setId(item.getOwner().getId());
            item.setOwner(owner);
        }
        Item updatedItem = itemService.update(item);
        return new ResponseEntity<>(updatedItem, HttpStatus.OK);
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        itemService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    @GetMapping("/find/{id}")
    public ResponseEntity<Item> findById(@PathVariable Integer id) {
        Optional<Item> item = itemService.findById(id);
        return item.map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @GetMapping("/findAll")
    public ResponseEntity<List<Item>> findAll() {
        List<Item> items = itemService.findAll();
        return new ResponseEntity<>(items, HttpStatus.OK);
    }
    
    @GetMapping("/findByCategory/{categoryId}")
    public ResponseEntity<List<Item>> findByCategory(@PathVariable Integer categoryId) {
        List<Item> items = itemService.findByCategory(categoryId);
        return new ResponseEntity<>(items, HttpStatus.OK);
    }
    
    @GetMapping("/findByOwner/{ownerId}")
    public ResponseEntity<List<Item>> findByOwner(@PathVariable Integer ownerId) {
        List<Item> items = itemService.findByOwner(ownerId);
        return new ResponseEntity<>(items, HttpStatus.OK);
    }
    
    @GetMapping("/findAvailable")
    public ResponseEntity<List<Item>> findAvailableItems() {
        List<Item> items = itemService.findAvailableItems();
        return new ResponseEntity<>(items, HttpStatus.OK);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Item>> searchItems(@RequestParam String keyword) {
        List<Item> items = itemService.searchItems(keyword);
        return new ResponseEntity<>(items, HttpStatus.OK);
    }
    
    @GetMapping("/topBorrowed")
    public ResponseEntity<List<Item>> findTopBorrowedItems(@RequestParam(defaultValue = "10") int limit) {
        List<Item> items = itemService.findTopBorrowedItems(limit);
        return new ResponseEntity<>(items, HttpStatus.OK);
    }
}