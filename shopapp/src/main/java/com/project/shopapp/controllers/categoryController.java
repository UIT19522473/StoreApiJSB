package com.project.shopapp.controllers;

import com.project.shopapp.dtos.CategoryDTO;
import com.project.shopapp.models.Category;
import com.project.shopapp.services.ICategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("api/v1/categories")
@RequiredArgsConstructor
public class categoryController {
    //get all categories

    private final ICategoryService categoryService;

    //insert categor
    @PostMapping("")
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryDTO categoryDTO, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
            return ResponseEntity.badRequest().body(errorMessages);
        }

        try {
            categoryService.createCategory(categoryDTO);
            return ResponseEntity.ok("Create category successfully" + categoryDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @GetMapping("")
    public ResponseEntity<?> getAllCategories(@RequestParam int page, @RequestParam int limit) {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }


    //update category by id
    @PutMapping("/{id}")
    public ResponseEntity<String> updateCategory(@PathVariable Long id,@Valid @RequestBody CategoryDTO categoryDTO) {

        Category updatedCategory = categoryService.updateCategory(id,categoryDTO);

        return ResponseEntity.ok("category is updated: " + updatedCategory);
    }

    //delete category
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("delete: " + id);
    }
}
