package com.campus.share.repository;

import com.campus.share.entity.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Integer> {
    List<BorrowRecord> findByBorrowerId(Integer borrowerId);
    List<BorrowRecord> findByLenderId(Integer lenderId);
    List<BorrowRecord> findByItemId(Integer itemId);
    List<BorrowRecord> findByStatus(BorrowRecord.Status status);
    List<BorrowRecord> findByBorrowerIdAndStatus(Integer borrowerId, BorrowRecord.Status status);
    List<BorrowRecord> findByLenderIdAndStatus(Integer lenderId, BorrowRecord.Status status);
}