package com.campus.share.service.impl;

import com.campus.share.entity.BorrowRecord;
import com.campus.share.entity.User;
import com.campus.share.repository.BorrowRecordRepository;
import com.campus.share.service.AIAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AIAuditServiceImpl implements AIAuditService {
    
    @Autowired
    private BorrowRecordRepository borrowRecordRepository;
    
    // 信用评分阈值
    private static final int HIGH_CREDIT_THRESHOLD = 80;
    private static final int LOW_CREDIT_THRESHOLD = 60;
    
    @Override
    public int calculateCreditScore(Integer userId) {
        List<BorrowRecord> borrowRecords = borrowRecordRepository.findByBorrowerId(userId);
        
        if (borrowRecords.isEmpty()) {
            return 70; // 新用户默认信用分
        }
        
        int totalBorrows = borrowRecords.size();
        int returnedOnTime = 0;
        int overdueCount = 0;
        
        for (BorrowRecord record : borrowRecords) {
            if (record.getActualReturnDate() != null) {
                if (record.getActualReturnDate().isAfter(record.getExpectedReturnDate())) {
                    overdueCount++;
                } else {
                    returnedOnTime++;
                }
            }
        }
        
        double returnRate = (double) returnedOnTime / totalBorrows;
        double overdueRate = (double) overdueCount / totalBorrows;
        
        // 计算信用分数
        int baseScore = 80;
        int borrowCountBonus = Math.min(totalBorrows * 2, 20); // 借用次数奖励，最多20分
        int returnRateScore = (int) (returnRate * 30); // 归还率得分
        int overduePenalty = (int) (overdueRate * 50); // 逾期惩罚
        
        int creditScore = baseScore + borrowCountBonus + returnRateScore - overduePenalty;
        
        // 确保分数在0-100之间
        return Math.max(0, Math.min(100, creditScore));
    }
    
    @Override
    public boolean autoAuditBorrowRequest(Integer userId, Integer itemId) {
        int creditScore = calculateCreditScore(userId);
        return creditScore >= HIGH_CREDIT_THRESHOLD;
    }
    
    @Override
    public String getCreditLevel(int creditScore) {
        if (creditScore >= HIGH_CREDIT_THRESHOLD) {
            return "高信用";
        } else if (creditScore >= LOW_CREDIT_THRESHOLD) {
            return "中等信用";
        } else {
            return "低信用";
        }
    }
    
    @Override
    public BorrowHistoryAnalysis analyzeBorrowHistory(User user) {
        List<BorrowRecord> borrowRecords = borrowRecordRepository.findByBorrowerId(user.getId());
        
        BorrowHistoryAnalysis analysis = new BorrowHistoryAnalysis();
        analysis.setTotalBorrows(borrowRecords.size());
        
        int returnedOnTime = 0;
        int overdueCount = 0;
        
        for (BorrowRecord record : borrowRecords) {
            if (record.getActualReturnDate() != null) {
                if (record.getActualReturnDate().isAfter(record.getExpectedReturnDate())) {
                    overdueCount++;
                } else {
                    returnedOnTime++;
                }
            }
        }
        
        analysis.setReturnedOnTime(returnedOnTime);
        analysis.setOverdueCount(overdueCount);
        
        if (borrowRecords.size() > 0) {
            analysis.setReturnRate((double) returnedOnTime / borrowRecords.size());
            analysis.setOverdueRate((double) overdueCount / borrowRecords.size());
        }
        
        return analysis;
    }
}
