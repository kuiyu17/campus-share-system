package com.campus.share.repository;

import com.campus.share.entity.Item;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findByCategoryId(Integer categoryId);
    
    List<Item> findByOwnerId(Integer ownerId);
    
    List<Item> findByStatus(Item.Status status);
    
    @Query("SELECT i FROM Item i WHERE i.status = ?1 ORDER BY COALESCE(i.borrowCount, 0) DESC")
    List<Item> findTopBorrowedItems(Item.Status status, org.springframework.data.domain.Pageable pageable);
    
    @Query("SELECT i FROM Item i WHERE i.status = ?1 AND (i.name LIKE %?2% OR i.description LIKE %?2%)")
    List<Item> searchItems(Item.Status status, String keyword);
    
    @Override
    Optional<Item> findById(Integer id);
    
    @Override
    List<Item> findAll();
}