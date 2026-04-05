package com.campus.share.service.impl;

import com.campus.share.entity.BorrowRecord;
import com.campus.share.entity.Item;
import com.campus.share.entity.User;
import com.campus.share.entity.AIRecommendation;
import com.campus.share.repository.BorrowRecordRepository;
import com.campus.share.repository.ItemRepository;
import com.campus.share.repository.UserRepository;
import com.campus.share.repository.AIRecommendationRepository;
import com.campus.share.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private BorrowRecordRepository borrowRecordRepository;
    
    @Autowired
    private ItemRepository itemRepository;
    
    @Autowired
    private AIRecommendationRepository aiRecommendationRepository;
    
    @Override
    public User register(User user) {
        // 密码加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    
    @Override
    public User login(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        return validateAndReturnUser(userOptional, password);
    }
    
    @Override
    public User loginByPhone(String phone, String password) {
        Optional<User> userOptional = userRepository.findByPhone(phone);
        return validateAndReturnUser(userOptional, password);
    }
    
    private User validateAndReturnUser(Optional<User> userOptional, String password) {
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // 检查密码是否为明文（长度小于60，BCrypt加密后长度为60）
            if (user.getPassword().length() < 60) {
                // 明文密码直接比较
                if (password.equals(user.getPassword())) {
                    return user;
                }
            } else {
                // 加密密码使用matches验证
                if (passwordEncoder.matches(password, user.getPassword())) {
                    return user;
                }
            }
        }
        return null;
    }
    
    @Override
    public User update(User user) {
        // 先查找现有用户
        Optional<User> existingUserOpt = userRepository.findById(user.getId());
        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            // 更新字段
            existingUser.setName(user.getName());
            existingUser.setEmail(user.getEmail());
            existingUser.setPhone(user.getPhone());
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            return userRepository.save(existingUser);
        }
        return null;
    }
    
    @Override
    @Transactional
    public void delete(Integer id) {
        // 1. 检查用户是否有未归还的物品（作为借用人）
        List<BorrowRecord> unreturnedBorrows = borrowRecordRepository.findByBorrowerIdAndStatus(id, BorrowRecord.Status.borrowed);
        if (!unreturnedBorrows.isEmpty()) {
            throw new RuntimeException("您有未归还的物品，请先归还后再注销账户");
        }
        
        // 2. 检查用户是否有未处理的借用请求（作为借用人）
        List<BorrowRecord> pendingBorrows = borrowRecordRepository.findByBorrowerIdAndStatus(id, BorrowRecord.Status.pending);
        if (!pendingBorrows.isEmpty()) {
            throw new RuntimeException("您有未处理的借用请求，请先处理后再注销账户");
        }
        
        // 3. 检查用户是否有物品被借出（作为出借人）
        List<BorrowRecord> lentItems = borrowRecordRepository.findByLenderIdAndStatus(id, BorrowRecord.Status.borrowed);
        if (!lentItems.isEmpty()) {
            throw new RuntimeException("您的物品正在被借用，请等待归还后再注销账户");
        }
        
        // 4. 检查用户是否有未处理的借用请求（作为出借人）
        List<BorrowRecord> pendingLends = borrowRecordRepository.findByLenderIdAndStatus(id, BorrowRecord.Status.pending);
        if (!pendingLends.isEmpty()) {
            throw new RuntimeException("您有未处理的借用请求，请先处理后再注销账户");
        }
        
        // 5. 删除用户发布的所有物品（此时物品应该都是可借用状态）
        List<Item> userItems = itemRepository.findByOwnerId(id);
        for (Item item : userItems) {
            // 先删除该物品相关的所有AI推荐记录
            aiRecommendationRepository.deleteByItemId(item.getId());
            // 先删除该物品相关的所有借用记录
            List<BorrowRecord> itemRecords = borrowRecordRepository.findByItemId(item.getId());
            for (BorrowRecord record : itemRecords) {
                borrowRecordRepository.delete(record);
            }
            itemRepository.delete(item);
        }
        
        // 6. 删除该用户作为借用人的所有借用记录
        List<BorrowRecord> borrowerRecords = borrowRecordRepository.findByBorrowerId(id);
        for (BorrowRecord record : borrowerRecords) {
            borrowRecordRepository.delete(record);
        }
        
        // 7. 删除该用户作为出借人的所有借用记录
        List<BorrowRecord> lenderRecords = borrowRecordRepository.findByLenderId(id);
        for (BorrowRecord record : lenderRecords) {
            borrowRecordRepository.delete(record);
        }
        
        // 8. 删除该用户的所有AI推荐记录
        List<AIRecommendation> aiRecommendations = aiRecommendationRepository.findByUserId(id);
        for (AIRecommendation recommendation : aiRecommendations) {
            aiRecommendationRepository.delete(recommendation);
        }
        
        // 9. 删除用户
        userRepository.deleteById(id);
    }
    
    @Override
    public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }
    
    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }
}
