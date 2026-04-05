package com.campus.share.repository;

import com.campus.share.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByItemId(Integer itemId);
    List<Review> findByUserId(Integer userId);
}