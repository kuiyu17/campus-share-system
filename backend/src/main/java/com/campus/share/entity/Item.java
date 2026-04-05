package com.campus.share.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    
    @Enumerated(EnumType.STRING)
    private Status status;
    
    private String location;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    private BigDecimal value;
    
    @Column(name = "borrow_count")
    private Integer borrowCount;
    
    private BigDecimal rating;
    
    private BigDecimal rentPerDay;
    
    private BigDecimal deposit;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum Status {
        available, borrowed, maintenance
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (borrowCount == null) {
            borrowCount = 0;
        }
        if (rating == null) {
            rating = BigDecimal.ZERO;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}