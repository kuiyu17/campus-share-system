package com.campus.share.controller;

import com.campus.share.entity.Item;
import com.campus.share.service.AIRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class AIRecommendationController {
    
    @Autowired
    private AIRecommendationService aiRecommendationService;
    
    @GetMapping("/recommendations/{userId}")
    public ResponseEntity<List<Item>> getRecommendations(@PathVariable Integer userId) {
        List<Item> recommendations = aiRecommendationService.getRecommendations(userId);
        return new ResponseEntity<>(recommendations, HttpStatus.OK);
    }
    
    @PostMapping("/update/{userId}")
    public ResponseEntity<Void> updateRecommendations(@PathVariable Integer userId) {
        aiRecommendationService.updateRecommendations(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @GetMapping("/personalized/{userId}")
    public ResponseEntity<List<Item>> getPersonalizedRecommendations(@PathVariable Integer userId) {
        List<Item> recommendations = aiRecommendationService.getPersonalizedRecommendations(userId);
        return new ResponseEntity<>(recommendations, HttpStatus.OK);
    }
    
    @GetMapping("/popular")
    public ResponseEntity<List<Item>> getPopularRecommendations() {
        List<Item> recommendations = aiRecommendationService.getPopularRecommendations();
        return new ResponseEntity<>(recommendations, HttpStatus.OK);
    }
    
    @GetMapping("/new")
    public ResponseEntity<List<Item>> getNewRecommendations() {
        List<Item> recommendations = aiRecommendationService.getNewRecommendations();
        return new ResponseEntity<>(recommendations, HttpStatus.OK);
    }
}