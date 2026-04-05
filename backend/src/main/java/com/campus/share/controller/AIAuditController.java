package com.campus.share.controller;

import com.campus.share.service.AIAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/audit")
public class AIAuditController {
    
    @Autowired
    private AIAuditService aiAuditService;
    
    @GetMapping("/credit-score/{userId}")
    public ResponseEntity<Integer> getCreditScore(@PathVariable Integer userId) {
        int creditScore = aiAuditService.calculateCreditScore(userId);
        return new ResponseEntity<>(creditScore, HttpStatus.OK);
    }
    
    @GetMapping("/credit-level/{userId}")
    public ResponseEntity<String> getCreditLevel(@PathVariable Integer userId) {
        int creditScore = aiAuditService.calculateCreditScore(userId);
        String creditLevel = aiAuditService.getCreditLevel(creditScore);
        return new ResponseEntity<>(creditLevel, HttpStatus.OK);
    }
    
    @PostMapping("/auto-audit")
    public ResponseEntity<Boolean> autoAuditBorrowRequest(
            @RequestParam Integer userId,
            @RequestParam Integer itemId) {
        boolean approved = aiAuditService.autoAuditBorrowRequest(userId, itemId);
        return new ResponseEntity<>(approved, HttpStatus.OK);
    }
    
    @GetMapping("/borrow-history/{userId}")
    public ResponseEntity<AIAuditService.BorrowHistoryAnalysis> getBorrowHistoryAnalysis(
            @PathVariable Integer userId) {
        // 这里需要获取用户对象，暂时简化处理
        // 实际项目中应该从数据库获取用户
        AIAuditService.BorrowHistoryAnalysis analysis = aiAuditService.analyzeBorrowHistory(null);
        return new ResponseEntity<>(analysis, HttpStatus.OK);
    }
}
