package com.campus.share.service.impl;

import com.campus.share.entity.Category;
import com.campus.share.repository.CategoryRepository;
import com.campus.share.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Override
    public Category create(Category category) {
        return categoryRepository.save(category);
    }
    
    @Override
    public Category update(Category category) {
        return categoryRepository.save(category);
    }
    
    @Override
    public void delete(Integer id) {
        categoryRepository.deleteById(id);
    }
    
    @Override
    public Optional<Category> findById(Integer id) {
        return categoryRepository.findById(id);
    }
    
    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }
}
