package com.campus.share.service;

import com.campus.share.entity.Item;
import java.util.List;

public interface AIRecommendationService {
    // 综合推荐：基于用户历史和热门度的智能推荐
    List<Item> getRecommendations(Integer userId);
    
    // 更新用户的推荐数据
    void updateRecommendations(Integer userId);
    
    // 个性化推荐：基于用户兴趣偏好的推荐
    List<Item> getPersonalizedRecommendations(Integer userId);
    
    // 热门推荐：最受欢迎的物品
    List<Item> getPopularRecommendations();
    
    // 新品推荐：最近发布的物品
    List<Item> getNewRecommendations();
}