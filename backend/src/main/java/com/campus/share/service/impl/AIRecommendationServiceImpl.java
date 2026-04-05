package com.campus.share.service.impl;

import com.campus.share.entity.AIRecommendation;
import com.campus.share.entity.Item;
import com.campus.share.entity.BorrowRecord;
import com.campus.share.entity.User;
import com.campus.share.repository.AIRecommendationRepository;
import com.campus.share.repository.BorrowRecordRepository;
import com.campus.share.repository.ItemRepository;
import com.campus.share.repository.UserRepository;
import com.campus.share.service.AIRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AIRecommendationServiceImpl implements AIRecommendationService {
    
    @Autowired
    private AIRecommendationRepository aiRecommendationRepository;
    
    @Autowired
    private BorrowRecordRepository borrowRecordRepository;
    
    @Autowired
    private ItemRepository itemRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public List<Item> getRecommendations(Integer userId) {
        List<AIRecommendation> recommendations = aiRecommendationRepository.findByUserId(userId);
        
        if (recommendations.isEmpty()) {
            updateRecommendations(userId);
            recommendations = aiRecommendationRepository.findByUserId(userId);
        }
        
        return recommendations.stream()
                .sorted((r1, r2) -> r2.getScore().compareTo(r1.getScore()))
                .limit(10)
                .map(AIRecommendation::getItem)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void updateRecommendations(Integer userId) {
        List<BorrowRecord> borrowHistory = borrowRecordRepository.findByBorrowerId(userId);
        List<Item> availableItems = itemRepository.findByStatus(Item.Status.available);
        
        if (availableItems.isEmpty()) {
            return;
        }
        
        Map<Item, BigDecimal> scores = new HashMap<>();
        
        Set<Integer> borrowedCategoryIds = new HashSet<>();
        for (BorrowRecord record : borrowHistory) {
            if (record.getItem() != null && record.getItem().getCategory() != null) {
                borrowedCategoryIds.add(record.getItem().getCategory().getId());
            }
        }
        
        for (Item item : availableItems) {
            BigDecimal score = BigDecimal.ZERO;
            
            score = score.add(BigDecimal.valueOf(item.getBorrowCount() * 0.4));
            
            if (item.getRating() != null) {
                score = score.add(item.getRating().multiply(BigDecimal.valueOf(0.3)));
            }
            
            if (item.getCategory() != null && borrowedCategoryIds.contains(item.getCategory().getId())) {
                score = score.add(BigDecimal.valueOf(0.3));
            }
            
            scores.put(item, score);
        }
        
        List<AIRecommendation> oldRecommendations = aiRecommendationRepository.findByUserId(userId);
        if (!oldRecommendations.isEmpty()) {
            aiRecommendationRepository.deleteAll(oldRecommendations);
        }
        
        User user = userRepository.findById(userId).orElse(null);
        if (user != null && !scores.isEmpty()) {
            List<AIRecommendation> newRecommendations = scores.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .limit(10)
                    .map(entry -> {
                        AIRecommendation recommendation = new AIRecommendation();
                        recommendation.setUser(user);
                        recommendation.setItem(entry.getKey());
                        recommendation.setScore(entry.getValue());
                        return recommendation;
                    })
                    .collect(Collectors.toList());
            
            aiRecommendationRepository.saveAll(newRecommendations);
        }
    }
    
    @Override
    public List<Item> getPersonalizedRecommendations(Integer userId) {
        List<BorrowRecord> borrowHistory = borrowRecordRepository.findByBorrowerId(userId);
        
        if (borrowHistory.isEmpty()) {
            return getPopularRecommendations();
        }
        
        Set<Integer> borrowedCategoryIds = new HashSet<>();
        for (BorrowRecord record : borrowHistory) {
            if (record.getItem() != null && record.getItem().getCategory() != null) {
                borrowedCategoryIds.add(record.getItem().getCategory().getId());
            }
        }
        
        if (borrowedCategoryIds.isEmpty()) {
            return getPopularRecommendations();
        }
        
        List<Item> availableItems = itemRepository.findByStatus(Item.Status.available);
        
        List<Item> filteredItems = availableItems.stream()
                .filter(item -> item.getCategory() != null && borrowedCategoryIds.contains(item.getCategory().getId()))
                .sorted((i1, i2) -> {
                    int score1 = i1.getBorrowCount() * 3;
                    int score2 = i2.getBorrowCount() * 3;
                    if (i1.getRating() != null) score1 += i1.getRating().multiply(BigDecimal.valueOf(2)).intValue();
                    if (i2.getRating() != null) score2 += i2.getRating().multiply(BigDecimal.valueOf(2)).intValue();
                    return score2 - score1;
                })
                .limit(10)
                .collect(Collectors.toList());
        
        // 如果过滤后没有物品，返回热门推荐
        if (filteredItems.isEmpty()) {
            return getPopularRecommendations();
        }
        
        return filteredItems;
    }
    
    @Override
    public List<Item> getPopularRecommendations() {
        return itemRepository.findByStatus(Item.Status.available)
                .stream()
                .sorted((i1, i2) -> i2.getBorrowCount() - i1.getBorrowCount())
                .limit(10)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Item> getNewRecommendations() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return itemRepository.findByStatus(Item.Status.available)
                .stream()
                .filter(item -> item.getCreatedAt() != null && item.getCreatedAt().isAfter(thirtyDaysAgo))
                .sorted((i1, i2) -> i2.getCreatedAt().compareTo(i1.getCreatedAt()))
                .limit(10)
                .collect(Collectors.toList());
    }
}
