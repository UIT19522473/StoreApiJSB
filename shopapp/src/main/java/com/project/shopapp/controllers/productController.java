package com.project.shopapp.controllers;

import com.github.javafaker.Faker;
import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.response.ProductListResponse;
import com.project.shopapp.response.ProductResponse;
import com.project.shopapp.services.IProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/products")
@RequiredArgsConstructor
public class productController {

    private final IProductService productService;

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(@Valid @ModelAttribute ProductDTO productDTO, @RequestParam("file") List<MultipartFile> fileUp, BindingResult result) {
        try {

            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }

            Product newProduct = productService.createProduct(productDTO);

//            List<MultipartFile> files = product.getFiles();
            List<MultipartFile> files = fileUp;
            files = files == null ? new ArrayList<MultipartFile>() : files;

            boolean checkInsertImageProduct = createProductImage(files, newProduct);

            return ResponseEntity.ok(newProduct.toString());

//
//            for (MultipartFile file : files) {
//
//                if (file.getSize() == 0) {
//                    continue;
//                }
//
//                if (file.getSize() > 10 * 1024 * 1024) {
//                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("File is too large! Maximum size is 10MB");
//
//                }
//                //kiem tra xem file co phai la anh
//                String contentType = file.getContentType();
//                if (contentType == null || !contentType.startsWith("image/")) {
//                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("File must be an image");
//
//                }
//
//                String filename = storeFile(file);
//                //luu vao product trong DB
//                ProductImage newProductImage = productService.createProductImage(newProduct.getId(), ProductImageDTO.builder()
//                        .productId(newProduct)
//                        .imageUrl(filename)
//                        .build());
//            }


        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private boolean createProductImage(List<MultipartFile> files, Product newProduct) throws RuntimeException {

        for (MultipartFile file : files) {

            if (file.getSize() == 0) {
                continue;
            }

            if (file.getSize() > 10 * 1024 * 1024) {
                throw new RuntimeException("File is too large! Maximum size is 10MB");
            }
            //kiem tra xem file co phai la anh
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new RuntimeException("File must be an image");

            }

            String filename = null;
            try {
                filename = storeFile(file);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
            //luu vao product trong DB
            ProductImage newProductImage = productService.createProductImage(newProduct.getId(), ProductImageDTO.builder()
                    .productId(newProduct)
                    .imageUrl(filename)
                    .build());
        }
        return true;
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


    //    @PostMapping("create-product-image/{productId}")
    @PostMapping(value = "create-product-image/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProductImageAPI(@RequestParam("file") List<MultipartFile> fileUp, @PathVariable Long productId) {

        List<MultipartFile> files = fileUp;
        files = files == null ? new ArrayList<MultipartFile>() : files;

        try {
            Product getProduct = productService.getProductById(productId);

            if (createProductImage(files, getProduct)) {
                return ResponseEntity.ok("create product image successfully");
            }
            return ResponseEntity.badRequest().body("Not upload image to database");

        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    //    get all products
    @GetMapping("")
    public ResponseEntity<?> getAllProducts(@RequestParam int page, @RequestParam int limit) {
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("createdAt").descending());
        Page<ProductResponse> productList = productService.getAllProducts(pageRequest);
        int totalPages = productList.getTotalPages();

//        build product list response including list products and total pages
        ProductListResponse result = ProductListResponse.builder()
                .products(productList.getContent())
                .totalPages(totalPages)
                .build();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) throws DataNotFoundException {

        try {
            Product getProduct = productService.getProductById(id);

            ProductResponse productResponse = ProductResponse.fromProduct(getProduct);

            return ResponseEntity.ok(productResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }


    //insert product
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) throws DataNotFoundException {

        Product updatedProduct = productService.updateProduct(id, productDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    //insert product
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Delete product with id: " + id + " successfully");
    }


    //    fake data products
    @PostMapping("/generated-fake-products")
    public ResponseEntity<?> generatedFakeProducts() {

        Faker faker = new Faker();
        for (int i = 0; i < 1000; i++) {
            String productName = faker.commerce().productName();
            if (productService.existsByName(productName)) {
                continue;
            }
            ProductDTO productDTO = ProductDTO.builder()
                    .name(productName)
                    .description(faker.lorem().sentence())
                    .price((float) faker.number().numberBetween(10, 500000))
                    .categoryId((long) faker.number().numberBetween(1, 5))
                    .thumbnail("")
                    .build();

            try {
                productService.createProduct(productDTO);
            } catch (DataNotFoundException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }

        return ResponseEntity.ok("Insert data fake for products successfully");
    }
}
