package com.campus.share.service.impl;

import com.campus.share.entity.Item;
import com.campus.share.entity.BorrowRecord;
import com.campus.share.repository.ItemRepository;
import com.campus.share.repository.BorrowRecordRepository;
import com.campus.share.service.AIDataAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AIDataAnalysisServiceImpl implements AIDataAnalysisService {
    
    @Autowired
    private ItemRepository itemRepository;
    
    @Autowired
    private BorrowRecordRepository borrowRecordRepository;
    
    @Override
    public List<ItemUsageRank> getItemUsageRanking(int limit) {
        List<Item> allItems = itemRepository.findAll();
        List<ItemUsageRank> usageRanks = new ArrayList<>();
        
        for (Item item : allItems) {
            int borrowCount = item.getBorrowCount();
            // 计算使用率（借用次数 / 物品存在天数）
            long daysSinceCreation = ChronoUnit.DAYS.between(
                    item.getCreatedAt(), LocalDateTime.now());
            double usageRate = daysSinceCreation > 0 ? 
                    (double) borrowCount / daysSinceCreation : borrowCount;
            
            ItemUsageRank rank = new ItemUsageRank();
            rank.setItem(item);
            rank.setBorrowCount(borrowCount);
            rank.setUsageRate(usageRate);
            rank.setCategoryName(item.getCategory() != null ? 
                    item.getCategory().getName() : "未分类");
            
            usageRanks.add(rank);
        }
        
        // 按借用次数排序
        usageRanks.sort((r1, r2) -> r2.getBorrowCount() - r1.getBorrowCount());
        
        return usageRanks.stream().limit(limit).collect(Collectors.toList());
    }
    
    @Override
    public List<HighDemandPrediction> predictHighDemandItems(int limit) {
        List<Item> availableItems = itemRepository.findByStatus(Item.Status.available);
        List<HighDemandPrediction> predictions = new ArrayList<>();
        
        for (Item item : availableItems) {
            // 基于借用次数和最近借用频率预测需求
            int borrowCount = item.getBorrowCount();
            
            // 计算最近30天的借用频率
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            // 暂时使用所有借用记录，后续可以在BorrowRecordRepository中添加相应方法
            List<BorrowRecord> allBorrows = borrowRecordRepository.findByItemId(item.getId());
            long recentBorrows = allBorrows.stream()
                    .filter(b -> b.getCreatedAt().isAfter(thirtyDaysAgo))
                    .count();
            
            double demandScore = borrowCount * 0.6 + recentBorrows * 0.4;
            
            HighDemandPrediction prediction = new HighDemandPrediction();
            prediction.setItem(item);
            prediction.setDemandScore(demandScore);
            
            // 预测趋势
            if (recentBorrows > borrowCount * 0.3) {
                prediction.setPredictedTrend("上升");
                prediction.setRecommendation("建议增加库存");
            } else if (recentBorrows < borrowCount * 0.1) {
                prediction.setPredictedTrend("下降");
                prediction.setRecommendation("保持现有库存");
            } else {
                prediction.setPredictedTrend("稳定");
                prediction.setRecommendation("维持现状");
            }
            
            predictions.add(prediction);
        }
        
        // 按需求分数排序
        predictions.sort((p1, p2) -> Double.compare(p2.getDemandScore(), p1.getDemandScore()));
        
        return predictions.stream().limit(limit).collect(Collectors.toList());
    }
    
    @Override
    public RateStatistics getRateStatistics() {
        List<BorrowRecord> allBorrows = borrowRecordRepository.findAll();
        RateStatistics statistics = new RateStatistics();
        
        statistics.setTotalBorrows(allBorrows.size());
        
        int overdueCount = 0;
        int damagedCount = 0;
        
        for (BorrowRecord record : allBorrows) {
            if (record.getActualReturnDate() != null && 
                record.getActualReturnDate().isAfter(record.getExpectedReturnDate())) {
                overdueCount++;
            }
        }
        
        statistics.setOverdueCount(overdueCount);
        statistics.setDamagedCount(damagedCount);
        
        if (allBorrows.size() > 0) {
            statistics.setOverdueRate((double) overdueCount / allBorrows.size());
            statistics.setDamageRate((double) damagedCount / allBorrows.size());
        }
        
        return statistics;
    }
    
    @Override
    public List<TrendData> getBorrowTrend(String period, int months) {
        List<TrendData> trendDataList = new ArrayList<>();
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(months);
        
        // 暂时使用所有借用记录，后续可以在BorrowRecordRepository中添加相应方法
        List<BorrowRecord> allBorrows = borrowRecordRepository.findAll();
        List<BorrowRecord> borrows = allBorrows.stream()
                .filter(b -> b.getCreatedAt().isAfter(startDate) && b.getCreatedAt().isBefore(endDate))
                .collect(Collectors.toList());
        
        if ("week".equals(period)) {
            // 按周统计
            Map<String, List<BorrowRecord>> weeklyBorrows = new HashMap<>();
            for (BorrowRecord borrow : borrows) {
                // 使用ISO周数
                String weekKey = borrow.getCreatedAt().getYear() + "-W" + 
                        borrow.getCreatedAt().get(java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR);
                weeklyBorrows.computeIfAbsent(weekKey, k -> new ArrayList<>()).add(borrow);
            }
            
            for (Map.Entry<String, List<BorrowRecord>> entry : weeklyBorrows.entrySet()) {
                TrendData data = new TrendData();
                data.setPeriod(entry.getKey());
                data.setBorrowCount(entry.getValue().size());
                data.setDate(entry.getValue().get(0).getCreatedAt());
                
                // 计算平均借用时长
                double totalDuration = 0;
                int returnedCount = 0;
                for (BorrowRecord borrow : entry.getValue()) {
                    if (borrow.getActualReturnDate() != null) {
                        totalDuration += ChronoUnit.DAYS.between(
                                borrow.getCreatedAt(), borrow.getActualReturnDate());
                        returnedCount++;
                    }
                }
                data.setAverageBorrowDuration(returnedCount > 0 ? 
                        totalDuration / returnedCount : 0);
                
                trendDataList.add(data);
            }
        } else {
            // 按月统计
            Map<String, List<BorrowRecord>> monthlyBorrows = new HashMap<>();
            for (BorrowRecord borrow : borrows) {
                String monthKey = borrow.getCreatedAt().getYear() + "-" + 
                        (borrow.getCreatedAt().getMonthValue() < 10 ? "0" : "") + 
                        borrow.getCreatedAt().getMonthValue();
                monthlyBorrows.computeIfAbsent(monthKey, k -> new ArrayList<>()).add(borrow);
            }
            
            for (Map.Entry<String, List<BorrowRecord>> entry : monthlyBorrows.entrySet()) {
                TrendData data = new TrendData();
                data.setPeriod(entry.getKey());
                data.setBorrowCount(entry.getValue().size());
                data.setDate(entry.getValue().get(0).getCreatedAt());
                
                // 计算平均借用时长
                double totalDuration = 0;
                int returnedCount = 0;
                for (BorrowRecord borrow : entry.getValue()) {
                    if (borrow.getActualReturnDate() != null) {
                        totalDuration += ChronoUnit.DAYS.between(
                                borrow.getCreatedAt(), borrow.getActualReturnDate());
                        returnedCount++;
                    }
                }
                data.setAverageBorrowDuration(returnedCount > 0 ? 
                        totalDuration / returnedCount : 0);
                
                trendDataList.add(data);
            }
        }
        
        // 按日期排序
        trendDataList.sort((d1, d2) -> d1.getDate().compareTo(d2.getDate()));
        
        return trendDataList;
    }
    
    @Override
    public UserBehaviorAnalysis analyzeUserBehavior(Integer userId) {
        List<BorrowRecord> userBorrows = borrowRecordRepository.findByBorrowerId(userId);
        UserBehaviorAnalysis analysis = new UserBehaviorAnalysis();
        
        analysis.setTotalBorrows(userBorrows.size());
        
        int returnedOnTimeCount = 0;
        double totalDuration = 0;
        Map<String, Integer> categoryBorrowCount = new HashMap<>();
        
        for (BorrowRecord borrow : userBorrows) {
            if (borrow.getActualReturnDate() != null) {
                if (borrow.getActualReturnDate().isBefore(borrow.getExpectedReturnDate()) || 
                    borrow.getActualReturnDate().isEqual(borrow.getExpectedReturnDate())) {
                    returnedOnTimeCount++;
                }
                totalDuration += ChronoUnit.DAYS.between(
                        borrow.getCreatedAt(), borrow.getActualReturnDate());
            }
            
            // 统计类别借用次数
            if (borrow.getItem() != null && borrow.getItem().getCategory() != null) {
                String categoryName = borrow.getItem().getCategory().getName();
                categoryBorrowCount.put(categoryName, 
                        categoryBorrowCount.getOrDefault(categoryName, 0) + 1);
            }
        }
        
        analysis.setReturnedOnTimeCount(returnedOnTimeCount);
        analysis.setReturnRate(userBorrows.size() > 0 ? 
                (double) returnedOnTimeCount / userBorrows.size() : 0);
        analysis.setAverageBorrowDuration(returnedOnTimeCount > 0 ? 
                totalDuration / returnedOnTimeCount : 0);
        analysis.setCategoryBorrowCount(categoryBorrowCount);
        
        // 找出用户偏好类别
        if (!categoryBorrowCount.isEmpty()) {
            String preferredCategory = categoryBorrowCount.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("无");
            analysis.setUserPreferenceCategory(preferredCategory);
        }
        
        return analysis;
    }
}
