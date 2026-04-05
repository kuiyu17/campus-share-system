package com.campus.share.service;

import com.campus.share.entity.BorrowRecord;
import com.campus.share.entity.User;

public interface AIAuditService {
    
    /**
     * 计算用户信用分数
     * @param userId 用户ID
     * @return 信用分数 (0-100)
     */
    int calculateCreditScore(Integer userId);
    
    /**
     * 自动审核借用请求
     * @param userId 用户ID
     * @param itemId 物品ID
     * @return true: 自动通过, false: 需要人工审核
     */
    boolean autoAuditBorrowRequest(Integer userId, Integer itemId);
    
    /**
     * 获取用户信用等级
     * @param creditScore 信用分数
     * @return 信用等级
     */
    String getCreditLevel(int creditScore);
    
    /**
     * 分析用户借用历史
     * @param user 用户
     * @return 借用历史分析结果
     */
    BorrowHistoryAnalysis analyzeBorrowHistory(User user);
    
    /**
     * 借用历史分析结果
     */
    class BorrowHistoryAnalysis {
        private int totalBorrows;
        private int returnedOnTime;
        private int overdueCount;
        private double returnRate;
        private double overdueRate;
        
        // getters and setters
        public int getTotalBorrows() { return totalBorrows; }
        public void setTotalBorrows(int totalBorrows) { this.totalBorrows = totalBorrows; }
        public int getReturnedOnTime() { return returnedOnTime; }
        public void setReturnedOnTime(int returnedOnTime) { this.returnedOnTime = returnedOnTime; }
        public int getOverdueCount() { return overdueCount; }
        public void setOverdueCount(int overdueCount) { this.overdueCount = overdueCount; }
        public double getReturnRate() { return returnRate; }
        public void setReturnRate(double returnRate) { this.returnRate = returnRate; }
        public double getOverdueRate() { return overdueRate; }
        public void setOverdueRate(double overdueRate) { this.overdueRate = overdueRate; }
    }
}
