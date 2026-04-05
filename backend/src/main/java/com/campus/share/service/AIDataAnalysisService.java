package com.campus.share.service;

import com.campus.share.entity.Item;
import com.campus.share.entity.BorrowRecord;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface AIDataAnalysisService {
    
    /**
     * 获取物品使用率排行榜
     * @param limit 限制数量
     * @return 物品使用率排行列表
     */
    List<ItemUsageRank> getItemUsageRanking(int limit);
    
    /**
     * 预测高需求物品
     * @param limit 限制数量
     * @return 高需求物品预测列表
     */
    List<HighDemandPrediction> predictHighDemandItems(int limit);
    
    /**
     * 统计逾期率和损坏率
     * @return 统计结果
     */
    RateStatistics getRateStatistics();
    
    /**
     * 获取借用趋势数据
     * @param period 周期 (week/month)
     * @param months 统计月数
     * @return 趋势数据
     */
    List<TrendData> getBorrowTrend(String period, int months);
    
    /**
     * 分析用户借用行为
     * @param userId 用户ID
     * @return 用户行为分析结果
     */
    UserBehaviorAnalysis analyzeUserBehavior(Integer userId);
    
    /**
     * 物品使用率排行项
     */
    class ItemUsageRank {
        private Item item;
        private int borrowCount;
        private double usageRate;
        private String categoryName;
        
        // getters and setters
        public Item getItem() { return item; }
        public void setItem(Item item) { this.item = item; }
        public int getBorrowCount() { return borrowCount; }
        public void setBorrowCount(int borrowCount) { this.borrowCount = borrowCount; }
        public double getUsageRate() { return usageRate; }
        public void setUsageRate(double usageRate) { this.usageRate = usageRate; }
        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    }
    
    /**
     * 高需求物品预测
     */
    class HighDemandPrediction {
        private Item item;
        private double demandScore;
        private String predictedTrend;
        private String recommendation;
        
        // getters and setters
        public Item getItem() { return item; }
        public void setItem(Item item) { this.item = item; }
        public double getDemandScore() { return demandScore; }
        public void setDemandScore(double demandScore) { this.demandScore = demandScore; }
        public String getPredictedTrend() { return predictedTrend; }
        public void setPredictedTrend(String predictedTrend) { this.predictedTrend = predictedTrend; }
        public String getRecommendation() { return recommendation; }
        public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    }
    
    /**
     * 率统计结果
     */
    class RateStatistics {
        private double overdueRate;
        private double damageRate;
        private int totalBorrows;
        private int overdueCount;
        private int damagedCount;
        
        // getters and setters
        public double getOverdueRate() { return overdueRate; }
        public void setOverdueRate(double overdueRate) { this.overdueRate = overdueRate; }
        public double getDamageRate() { return damageRate; }
        public void setDamageRate(double damageRate) { this.damageRate = damageRate; }
        public int getTotalBorrows() { return totalBorrows; }
        public void setTotalBorrows(int totalBorrows) { this.totalBorrows = totalBorrows; }
        public int getOverdueCount() { return overdueCount; }
        public void setOverdueCount(int overdueCount) { this.overdueCount = overdueCount; }
        public int getDamagedCount() { return damagedCount; }
        public void setDamagedCount(int damagedCount) { this.damagedCount = damagedCount; }
    }
    
    /**
     * 趋势数据
     */
    class TrendData {
        private String period;
        private int borrowCount;
        private LocalDateTime date;
        private double averageBorrowDuration;
        
        // getters and setters
        public String getPeriod() { return period; }
        public void setPeriod(String period) { this.period = period; }
        public int getBorrowCount() { return borrowCount; }
        public void setBorrowCount(int borrowCount) { this.borrowCount = borrowCount; }
        public LocalDateTime getDate() { return date; }
        public void setDate(LocalDateTime date) { this.date = date; }
        public double getAverageBorrowDuration() { return averageBorrowDuration; }
        public void setAverageBorrowDuration(double averageBorrowDuration) { this.averageBorrowDuration = averageBorrowDuration; }
    }
    
    /**
     * 用户行为分析
     */
    class UserBehaviorAnalysis {
        private String userPreferenceCategory;
        private double averageBorrowDuration;
        private int totalBorrows;
        private int returnedOnTimeCount;
        private double returnRate;
        private Map<String, Integer> categoryBorrowCount;
        
        // getters and setters
        public String getUserPreferenceCategory() { return userPreferenceCategory; }
        public void setUserPreferenceCategory(String userPreferenceCategory) { this.userPreferenceCategory = userPreferenceCategory; }
        public double getAverageBorrowDuration() { return averageBorrowDuration; }
        public void setAverageBorrowDuration(double averageBorrowDuration) { this.averageBorrowDuration = averageBorrowDuration; }
        public int getTotalBorrows() { return totalBorrows; }
        public void setTotalBorrows(int totalBorrows) { this.totalBorrows = totalBorrows; }
        public int getReturnedOnTimeCount() { return returnedOnTimeCount; }
        public void setReturnedOnTimeCount(int returnedOnTimeCount) { this.returnedOnTimeCount = returnedOnTimeCount; }
        public double getReturnRate() { return returnRate; }
        public void setReturnRate(double returnRate) { this.returnRate = returnRate; }
        public Map<String, Integer> getCategoryBorrowCount() { return categoryBorrowCount; }
        public void setCategoryBorrowCount(Map<String, Integer> categoryBorrowCount) { this.categoryBorrowCount = categoryBorrowCount; }
    }
}
