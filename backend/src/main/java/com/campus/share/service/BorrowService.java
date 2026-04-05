package com.campus.share.service;

import com.campus.share.entity.BorrowRecord;
import java.util.List;
import java.util.Optional;

public interface BorrowService {
    BorrowRecord create(BorrowRecord record);
    BorrowRecord update(BorrowRecord record);
    void delete(Integer id);
    Optional<BorrowRecord> findById(Integer id);
    List<BorrowRecord> findAll();
    List<BorrowRecord> findByBorrower(Integer borrowerId);
    List<BorrowRecord> findByLender(Integer lenderId);
    List<BorrowRecord> findByItem(Integer itemId);
    List<BorrowRecord> findPendingRequests(Integer lenderId);
    List<BorrowRecord> findBorrowedItems(Integer borrowerId);
    List<BorrowRecord> findOverdueItems();
    void checkOverdueItems();
    void penalizeOverdueBorrowers();
    BorrowRecord approve(Integer id);
    BorrowRecord reject(Integer id);
    BorrowRecord returnItem(Integer id);
}