package com.campus.share.service;

import com.campus.share.entity.Alert;
import com.campus.share.entity.BorrowRecord;
import com.campus.share.entity.User;
import java.util.List;

public interface AIAlertService {
    
    /**
     * 发送借用成功提醒
     * @param borrowRecord 借用记录
     */
    void sendBorrowSuccessAlert(BorrowRecord borrowRecord);
    
    /**
     * 发送即将逾期提醒
     * @param borrowRecord 借用记录
     */
    void sendUpcomingOverdueAlert(BorrowRecord borrowRecord);
    
    /**
     * 发送逾期提醒
     * @param borrowRecord 借用记录
     */
    void sendOverdueAlert(BorrowRecord borrowRecord);
    
    /**
     * 发送归还感谢与评价提醒
     * @param borrowRecord 借用记录
     */
    void sendReturnThankYouAlert(BorrowRecord borrowRecord);
    
    /**
     * 检查并发送所有需要的提醒
     */
    void checkAndSendAlerts();
    
    /**
     * 生成提醒消息
     * @param type 提醒类型
     * @param borrowRecord 借用记录
     * @return 提醒消息内容
     */
    String generateAlertMessage(String type, BorrowRecord borrowRecord);
    
    /**
     * 发送消息给用户
     * @param user 用户
     * @param message 消息内容
     * @param subject 消息主题
     */
    void sendMessageToUser(User user, String message, String subject);
    
    /**
     * 获取用户的提醒列表
     * @param userId 用户ID
     * @return 提醒列表
     */
    List<Alert> getUserAlerts(Integer userId);
    
    /**
     * 标记提醒为已读
     * @param alertId 提醒ID
     */
    void markAlertAsRead(Integer alertId);
    
    /**
     * 获取用户未读提醒数量
     * @param userId 用户ID
     * @return 未读提醒数量
     */
    long getUnreadAlertCount(Integer userId);
}
