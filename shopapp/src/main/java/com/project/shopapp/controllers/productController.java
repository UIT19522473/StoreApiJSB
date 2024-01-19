package com.project.shopapp.controllers;

import com.project.shopapp.dtos.CategoryDTO;
import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.TestDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/products")
public class productController {
    @GetMapping("")
    public ResponseEntity<String> getAllProducts(@RequestParam int page, @RequestParam int limit) {
        return ResponseEntity.ok("Get:..." + page + "--" + limit);
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok("Get product by id:..." + id);
    }

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(@Valid @ModelAttribute ProductDTO product, BindingResult result) {
        try {

            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }

            List<MultipartFile> files = product.getFiles();
            files = files == null ? new ArrayList<MultipartFile>() : files;

            for (MultipartFile file : files) {

                if (file.getSize() == 0) {
                    continue;
                }

                if (file.getSize() > 10 * 1024 * 1024) {
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("File is too large! Maximum size is 10MB");

                }
                //kiem tra xem file co phai la anh
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("File must be an image");

                }

                String filename = storeFile(file);
                //luu vao product trong DB => lam sau

            }

            return ResponseEntity.ok("Post:...");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private String storeFile(MultipartFile file) throws IOException {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        //them uuid vao truoc file de dam bao file la duy nhat
        String uniqueFilename = UUID.randomUUID().toString() + "_" + filename;
        java.nio.file.Path uploadDir = Paths.get("uploads");

        //kiem tra va tao thu muc neu no khong ton tai
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        //duong dan day du den file
        java.nio.file.Path destination = Paths.get(uploadDir.toString(), uniqueFilename);

        //sao chep file vao thu muc uploads
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFilename;
    }

    //insert product
    @PutMapping("/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable Long id) {
        return ResponseEntity.ok("updated: " + id);
    }

    //insert product
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        return ResponseEntity.ok("delete: " + id);
    }
}
