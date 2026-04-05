package com.campus.share.service;

import com.campus.share.entity.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryService {
    Category create(Category category);
    Category update(Category category);
    void delete(Integer id);
    Optional<Category> findById(Integer id);
    List<Category> findAll();
}
