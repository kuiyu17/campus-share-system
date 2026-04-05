package com.campus.share.service.impl;

import com.campus.share.entity.BorrowRecord;
import com.campus.share.entity.Item;
import com.campus.share.entity.User;
import com.campus.share.repository.BorrowRecordRepository;
import com.campus.share.repository.ItemRepository;
import com.campus.share.repository.UserRepository;
import com.campus.share.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BorrowServiceImpl implements BorrowService {
    
    @Autowired
    private BorrowRecordRepository borrowRecordRepository;
    
    @Autowired
    private ItemRepository itemRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public BorrowRecord create(BorrowRecord record) {
        // 设置物品的所有者为出借人
        if (record.getItem() != null && record.getItem().getId() != null) {
            Item item = itemRepository.findById(record.getItem().getId()).orElse(null);
            if (item != null) {
                record.setLender(item.getOwner());
            }
        }
        return borrowRecordRepository.save(record);
    }
    
    @Override
    public BorrowRecord update(BorrowRecord record) {
        return borrowRecordRepository.save(record);
    }
    
    @Override
    public void delete(Integer id) {
        borrowRecordRepository.deleteById(id);
    }
    
    @Override
    public Optional<BorrowRecord> findById(Integer id) {
        return borrowRecordRepository.findById(id);
    }
    
    @Override
    public List<BorrowRecord> findAll() {
        return borrowRecordRepository.findAll();
    }
    
    @Override
    public List<BorrowRecord> findByBorrower(Integer borrowerId) {
        return borrowRecordRepository.findByBorrowerId(borrowerId);
    }
    
    @Override
    public List<BorrowRecord> findByLender(Integer lenderId) {
        return borrowRecordRepository.findByLenderId(lenderId);
    }
    
    @Override
    public List<BorrowRecord> findByItem(Integer itemId) {
        return borrowRecordRepository.findByItemId(itemId);
    }
    
    @Override
    public List<BorrowRecord> findPendingRequests(Integer lenderId) {
        return borrowRecordRepository.findByLenderIdAndStatus(lenderId, BorrowRecord.Status.pending);
    }
    
    @Override
    public List<BorrowRecord> findBorrowedItems(Integer borrowerId) {
        return borrowRecordRepository.findByBorrowerIdAndStatus(borrowerId, BorrowRecord.Status.borrowed);
    }
    
    @Override
    public BorrowRecord approve(Integer id) {
        Optional<BorrowRecord> recordOptional = borrowRecordRepository.findById(id);
        if (recordOptional.isPresent()) {
            BorrowRecord record = recordOptional.get();
            record.setStatus(BorrowRecord.Status.approved);
            
            // 不需要更新物品状态为已借出，因为批准后还需要实际借出
            
            return borrowRecordRepository.save(record);
        }
        return null;
    }
    
    @Override
    public BorrowRecord reject(Integer id) {
        Optional<BorrowRecord> recordOptional = borrowRecordRepository.findById(id);
        if (recordOptional.isPresent()) {
            BorrowRecord record = recordOptional.get();
            record.setStatus(BorrowRecord.Status.rejected);
            return borrowRecordRepository.save(record);
        }
        return null;
    }
    
    @Override
    public BorrowRecord returnItem(Integer id) {
        Optional<BorrowRecord> recordOptional = borrowRecordRepository.findById(id);
        if (recordOptional.isPresent()) {
            BorrowRecord record = recordOptional.get();
            record.setStatus(BorrowRecord.Status.returned);
            record.setActualReturnDate(LocalDateTime.now());
            
            // 更新物品状态为可用
            Item item = record.getItem();
            item.setStatus(Item.Status.available);
            itemRepository.save(item);
            
            return borrowRecordRepository.save(record);
        }
        return null;
    }
    
    @Override
    public List<BorrowRecord> findOverdueItems() {
        List<BorrowRecord> borrowedItems = borrowRecordRepository.findByStatus(BorrowRecord.Status.borrowed);
        List<BorrowRecord> overdueItems = borrowRecordRepository.findByStatus(BorrowRecord.Status.overdue);
        LocalDateTime now = LocalDateTime.now();
        
        // 合并两种状态的记录，筛选出逾期的
        List<BorrowRecord> allRecords = new ArrayList<>();
        allRecords.addAll(borrowedItems);
        allRecords.addAll(overdueItems);
        
        return allRecords.stream()
                .filter(record -> now.isAfter(record.getExpectedReturnDate()))
                .toList();
    }
    
    @Override
    public void checkOverdueItems() {
        List<BorrowRecord> borrowedItems = borrowRecordRepository.findByStatus(BorrowRecord.Status.borrowed);
        LocalDateTime now = LocalDateTime.now();
        
        for (BorrowRecord record : borrowedItems) {
            if (now.isAfter(record.getExpectedReturnDate())) {
                record.setStatus(BorrowRecord.Status.overdue);
                borrowRecordRepository.save(record);
            }
        }
    }
    
    @Override
    public void penalizeOverdueBorrowers() {
        List<BorrowRecord> overdueItems = findOverdueItems();
        LocalDateTime now = LocalDateTime.now();
        
        for (BorrowRecord record : overdueItems) {
            User borrower = record.getBorrower();
            if (borrower != null) {
                long daysOverdue = ChronoUnit.DAYS.between(record.getExpectedReturnDate(), now);
                int penalty = 0;
                
                if (daysOverdue <= 3) {
                    penalty = 5;
                } else if (daysOverdue <= 7) {
                    penalty = 15;
                } else {
                    penalty = 30;
                }
                
                Integer currentScore = borrower.getCreditScore();
                if (currentScore == null) {
                    currentScore = 100;
                }
                
                int newScore = Math.max(0, currentScore - penalty);
                borrower.setCreditScore(newScore);
                userRepository.save(borrower);
                
                System.out.println("[信誉分处罚] 用户名: " + borrower.getName() + ", 逾期天数: " + daysOverdue + ", 扣减分数: " + penalty + ", 新分数: " + newScore);
            }
        }
    }
}