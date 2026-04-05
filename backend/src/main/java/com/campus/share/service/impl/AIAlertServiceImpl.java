package com.campus.share.service.impl;

import com.campus.share.entity.Alert;
import com.campus.share.entity.BorrowRecord;
import com.campus.share.entity.User;
import com.campus.share.repository.AlertRepository;
import com.campus.share.repository.BorrowRecordRepository;
import com.campus.share.repository.UserRepository;
import com.campus.share.service.AIAlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class AIAlertServiceImpl implements AIAlertService {
    
    @Autowired
    private BorrowRecordRepository borrowRecordRepository;
    
    @Autowired
    private AlertRepository alertRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // 即将逾期提醒的天数
    private static final int UPCOMING_OVERDUE_DAYS = 2;
    
    @Override
    public void sendBorrowSuccessAlert(BorrowRecord borrowRecord) {
        String message = generateAlertMessage("borrow_success", borrowRecord);
        sendMessageToUser(borrowRecord.getBorrower(), message, "借用成功提醒");
    }
    
    @Override
    public void sendUpcomingOverdueAlert(BorrowRecord borrowRecord) {
        String message = generateAlertMessage("upcoming_overdue", borrowRecord);
        sendMessageToUser(borrowRecord.getBorrower(), message, "即将逾期提醒");
    }
    
    @Override
    public void sendOverdueAlert(BorrowRecord borrowRecord) {
        String message = generateAlertMessage("overdue", borrowRecord);
        sendMessageToUser(borrowRecord.getBorrower(), message, "逾期提醒");
    }
    
    @Override
    public void sendReturnThankYouAlert(BorrowRecord borrowRecord) {
        String message = generateAlertMessage("return_thank_you", borrowRecord);
        sendMessageToUser(borrowRecord.getBorrower(), message, "归还感谢");
    }
    
    @Override
    public void checkAndSendAlerts() {
        List<BorrowRecord> activeBorrows = borrowRecordRepository.findByStatus(BorrowRecord.Status.borrowed);
        
        for (BorrowRecord record : activeBorrows) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expectedReturnDate = record.getExpectedReturnDate();
            
            // 检查是否即将逾期
            long daysUntilOverdue = ChronoUnit.DAYS.between(now, expectedReturnDate);
            if (daysUntilOverdue == UPCOMING_OVERDUE_DAYS) {
                sendUpcomingOverdueAlert(record);
            }
            
            // 检查是否已经逾期
            if (now.isAfter(expectedReturnDate)) {
                sendOverdueAlert(record);
            }
        }
    }
    
    @Override
    public String generateAlertMessage(String type, BorrowRecord borrowRecord) {
        switch (type) {
            case "borrow_success":
                return "尊敬的" + borrowRecord.getBorrower().getName() + "，您已成功借用\"" + 
                       borrowRecord.getItem().getName() + "\"，预计归还日期：" + 
                       borrowRecord.getExpectedReturnDate().toString();
            
            case "upcoming_overdue":
                return "尊敬的" + borrowRecord.getBorrower().getName() + "，您借用的\"" + 
                       borrowRecord.getItem().getName() + "\"将在2天后逾期，请及时归还。";
            
            case "overdue":
                return "尊敬的" + borrowRecord.getBorrower().getName() + "，您借用的\"" + 
                       borrowRecord.getItem().getName() + "\"已逾期，请立即归还，以免影响您的信用。";
            
            case "return_thank_you":
                return "尊敬的" + borrowRecord.getBorrower().getName() + "，感谢您及时归还\"" + 
                       borrowRecord.getItem().getName() + "\"。请对本次借用体验进行评价，您的反馈对我们很重要。";
            
            default:
                return "系统提醒";
        }
    }
    
    @Override
    public void sendMessageToUser(User user, String message, String subject) {
        // 创建提醒记录
        Alert alert = new Alert();
        alert.setUser(user);
        alert.setTitle(subject);
        alert.setMessage(message);
        
        // 根据主题设置提醒类型和优先级
        if (subject.contains("逾期")) {
            if (subject.contains("已逾期")) {
                alert.setType(Alert.Type.error);
                alert.setPriority(Alert.Priority.high);
            } else {
                alert.setType(Alert.Type.warning);
                alert.setPriority(Alert.Priority.medium);
            }
        } else if (subject.contains("成功")) {
            alert.setType(Alert.Type.success);
            alert.setPriority(Alert.Priority.low);
        } else {
            alert.setType(Alert.Type.info);
            alert.setPriority(Alert.Priority.low);
        }
        
        // 保存提醒到数据库
        alertRepository.save(alert);
        
        // 打印提醒信息
        System.out.println("[AI提醒] 发送给" + user.getName() + "的提醒:");
        System.out.println("主题: " + subject);
        System.out.println("内容: " + message);
        System.out.println("------------------------");
    }
    
    @Override
    public List<Alert> getUserAlerts(Integer userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            return alertRepository.findByUser(user);
        }
        return null;
    }
    
    @Override
    public void markAlertAsRead(Integer alertId) {
        Alert alert = alertRepository.findById(alertId).orElse(null);
        if (alert != null) {
            alert.setStatus(Alert.Status.read);
            alertRepository.save(alert);
        }
    }
    
    @Override
    public long getUnreadAlertCount(Integer userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            return alertRepository.countByUserAndStatus(user, Alert.Status.unread);
        }
        return 0;
    }
}
