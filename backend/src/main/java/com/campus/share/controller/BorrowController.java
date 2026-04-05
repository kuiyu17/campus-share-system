package com.campus.share.controller;

import com.campus.share.entity.BorrowRecord;
import com.campus.share.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/borrow")
public class BorrowController {
    
    @Autowired
    private BorrowService borrowService;
    
    @PostMapping("/create")
    public ResponseEntity<BorrowRecord> create(@RequestBody BorrowRecord record) {
        BorrowRecord createdRecord = borrowService.create(record);
        return new ResponseEntity<>(createdRecord, HttpStatus.CREATED);
    }
    
    @PutMapping("/update")
    public ResponseEntity<BorrowRecord> update(@RequestBody BorrowRecord record) {
        BorrowRecord updatedRecord = borrowService.update(record);
        return new ResponseEntity<>(updatedRecord, HttpStatus.OK);
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        borrowService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    @GetMapping("/find/{id}")
    public ResponseEntity<BorrowRecord> findById(@PathVariable Integer id) {
        Optional<BorrowRecord> record = borrowService.findById(id);
        return record.map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @GetMapping("/findAll")
    public ResponseEntity<List<BorrowRecord>> findAll() {
        List<BorrowRecord> records = borrowService.findAll();
        return new ResponseEntity<>(records, HttpStatus.OK);
    }
    
    @GetMapping("/findByBorrower/{borrowerId}")
    public ResponseEntity<List<BorrowRecord>> findByBorrower(@PathVariable Integer borrowerId) {
        List<BorrowRecord> records = borrowService.findByBorrower(borrowerId);
        return new ResponseEntity<>(records, HttpStatus.OK);
    }
    
    @GetMapping("/findByLender/{lenderId}")
    public ResponseEntity<List<BorrowRecord>> findByLender(@PathVariable Integer lenderId) {
        List<BorrowRecord> records = borrowService.findByLender(lenderId);
        return new ResponseEntity<>(records, HttpStatus.OK);
    }
    
    @GetMapping("/findByItem/{itemId}")
    public ResponseEntity<List<BorrowRecord>> findByItem(@PathVariable Integer itemId) {
        List<BorrowRecord> records = borrowService.findByItem(itemId);
        return new ResponseEntity<>(records, HttpStatus.OK);
    }
    
    @GetMapping("/pending/{lenderId}")
    public ResponseEntity<List<BorrowRecord>> findPendingRequests(@PathVariable Integer lenderId) {
        List<BorrowRecord> records = borrowService.findPendingRequests(lenderId);
        return new ResponseEntity<>(records, HttpStatus.OK);
    }
    
    @GetMapping("/borrowed/{borrowerId}")
    public ResponseEntity<List<BorrowRecord>> findBorrowedItems(@PathVariable Integer borrowerId) {
        List<BorrowRecord> records = borrowService.findBorrowedItems(borrowerId);
        return new ResponseEntity<>(records, HttpStatus.OK);
    }
    
    @GetMapping("/overdue")
    public ResponseEntity<List<BorrowRecord>> findOverdueItems() {
        List<BorrowRecord> records = borrowService.findOverdueItems();
        return new ResponseEntity<>(records, HttpStatus.OK);
    }
    
    @PostMapping("/checkOverdue")
    public ResponseEntity<Void> checkOverdueItems() {
        borrowService.checkOverdueItems();
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @PostMapping("/penalizeOverdue")
    public ResponseEntity<Void> penalizeOverdueBorrowers() {
        borrowService.penalizeOverdueBorrowers();
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @PutMapping("/approve/{id}")
    public ResponseEntity<BorrowRecord> approve(@PathVariable Integer id) {
        BorrowRecord record = borrowService.approve(id);
        return new ResponseEntity<>(record, HttpStatus.OK);
    }
    
    @PutMapping("/reject/{id}")
    public ResponseEntity<BorrowRecord> reject(@PathVariable Integer id) {
        BorrowRecord record = borrowService.reject(id);
        return new ResponseEntity<>(record, HttpStatus.OK);
    }
    
    @PutMapping("/return/{id}")
    public ResponseEntity<BorrowRecord> returnItem(@PathVariable Integer id) {
        BorrowRecord record = borrowService.returnItem(id);
        return new ResponseEntity<>(record, HttpStatus.OK);
    }
}