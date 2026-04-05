package com.campus.share.repository;

import com.campus.share.entity.AIRecommendation;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AIRecommendationRepository extends JpaRepository<AIRecommendation, Integer> {
    @EntityGraph(attributePaths = {"item", "item.category", "item.owner"})
    List<AIRecommendation> findByUserId(Integer userId);
    
    // 根据物品ID删除推荐记录
    void deleteByItemId(Integer itemId);
}