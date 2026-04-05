package com.campus.share.controller;

import com.campus.share.service.AIDataAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai/analysis")
public class AIDataAnalysisController {
    
    @Autowired
    private AIDataAnalysisService aiDataAnalysisService;
    
    @GetMapping("/usage-ranking")
    public ResponseEntity<List<AIDataAnalysisService.ItemUsageRank>> getItemUsageRanking(
            @RequestParam(defaultValue = "10") int limit) {
        List<AIDataAnalysisService.ItemUsageRank> ranking = aiDataAnalysisService.getItemUsageRanking(limit);
        return new ResponseEntity<>(ranking, HttpStatus.OK);
    }
    
    @GetMapping("/high-demand")
    public ResponseEntity<List<AIDataAnalysisService.HighDemandPrediction>> getHighDemandItems(
            @RequestParam(defaultValue = "10") int limit) {
        List<AIDataAnalysisService.HighDemandPrediction> predictions = aiDataAnalysisService.predictHighDemandItems(limit);
        return new ResponseEntity<>(predictions, HttpStatus.OK);
    }
    
    @GetMapping("/rate-statistics")
    public ResponseEntity<AIDataAnalysisService.RateStatistics> getRateStatistics() {
        AIDataAnalysisService.RateStatistics statistics = aiDataAnalysisService.getRateStatistics();
        return new ResponseEntity<>(statistics, HttpStatus.OK);
    }
    
    @GetMapping("/borrow-trend")
    public ResponseEntity<List<AIDataAnalysisService.TrendData>> getBorrowTrend(
            @RequestParam(defaultValue = "month") String period,
            @RequestParam(defaultValue = "6") int months) {
        List<AIDataAnalysisService.TrendData> trendData = aiDataAnalysisService.getBorrowTrend(period, months);
        return new ResponseEntity<>(trendData, HttpStatus.OK);
    }
    
    @GetMapping("/user-behavior/{userId}")
    public ResponseEntity<AIDataAnalysisService.UserBehaviorAnalysis> getUserBehaviorAnalysis(
            @PathVariable Integer userId) {
        AIDataAnalysisService.UserBehaviorAnalysis analysis = aiDataAnalysisService.analyzeUserBehavior(userId);
        return new ResponseEntity<>(analysis, HttpStatus.OK);
    }
}
