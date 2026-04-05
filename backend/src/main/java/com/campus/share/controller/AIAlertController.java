package com.campus.share.controller;

import com.campus.share.entity.Alert;
import com.campus.share.service.AIAlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai/alert")
public class AIAlertController {
    
    @Autowired
    private AIAlertService aiAlertService;
    
    @PostMapping("/check")
    public ResponseEntity<Void> checkAndSendAlerts() {
        aiAlertService.checkAndSendAlerts();
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @PostMapping("/test")
    public ResponseEntity<String> testAlert(
            @RequestParam String type,
            @RequestParam Integer borrowRecordId) {
        // 这里需要获取借用记录，暂时简化处理
        // 实际项目中应该从数据库获取借用记录
        return new ResponseEntity<>("测试提醒功能", HttpStatus.OK);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Alert>> getUserAlerts(@PathVariable Integer userId) {
        List<Alert> alerts = aiAlertService.getUserAlerts(userId);
        return new ResponseEntity<>(alerts, HttpStatus.OK);
    }
    
    @PutMapping("/mark-read/{alertId}")
    public ResponseEntity<Void> markAlertAsRead(@PathVariable Integer alertId) {
        aiAlertService.markAlertAsRead(alertId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @GetMapping("/unread-count/{userId}")
    public ResponseEntity<Long> getUnreadAlertCount(@PathVariable Integer userId) {
        long count = aiAlertService.getUnreadAlertCount(userId);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }
}
