package com.campus.share.repository;

import com.campus.share.entity.Alert;
import com.campus.share.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Integer> {
    
    /**
     * 根据用户查询提醒列表
     * @param user 用户
     * @return 提醒列表
     */
    List<Alert> findByUser(User user);
    
    /**
     * 根据用户和状态查询提醒列表
     * @param user 用户
     * @param status 提醒状态
     * @return 提醒列表
     */
    List<Alert> findByUserAndStatus(User user, Alert.Status status);
    
    /**
     * 统计用户未读提醒数量
     * @param user 用户
     * @return 未读提醒数量
     */
    @Query("SELECT COUNT(a) FROM Alert a WHERE a.user = :user AND a.status = :status")
    long countByUserAndStatus(@Param("user") User user, @Param("status") Alert.Status status);
    
    /**
     * 根据借用记录查询提醒列表
     * @param borrowRecordId 借用记录ID
     * @return 提醒列表
     */
    List<Alert> findByBorrowRecordId(Integer borrowRecordId);
}