package com.project.shopapp.services;

import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.repositories.ProductImageRepository;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.repositories.CategoryRepository;
import com.project.shopapp.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Category;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;

    @Override
    public Product createProduct(ProductDTO productDTO) throws DataNotFoundException {

        Category existingCategory = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException(
                        "Cannot find category with id: " + productDTO.getCategoryId()
                ));

        Product newProduct = Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .description(productDTO.getDescription())
                .thumbnail(productDTO.getThumbnail())
                .categoryId(existingCategory)
                .build();

        return productRepository.save(newProduct);
    }

    @Override
    public Product getProductById(Long id) throws DataNotFoundException {
        return productRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Cannot find product by id: " + id));
    }

    @Override
    public Page<ProductResponse> getAllProducts(PageRequest pageRequest) {
        //            ProductResponse productResponse = ProductResponse.builder()
        //                    .id(product.getId())
        //                    .name(product.getName())
        //                    .price(product.getPrice())
        //                    .thumbnail(product.getThumbnail())
        //                    .description(product.getDescription())
        //                    .categoryId(product.getCategoryId().getId())
        //                    .build();
        //
        //            productResponse.setCreatedAt(product.getCreatedAt());
        //            productResponse.setUpdatedAt(product.getUpdatedAt());
        //            return productResponse;

        return productRepository.findAll(pageRequest).map(ProductResponse::fromProduct);
    }

    @Override
    public Product updateProduct(Long id, ProductDTO productDTO) throws DataNotFoundException {
        Product existingProduct = getProductById(id);
        if (existingProduct != null) {
            Category existingCategory = categoryRepository.findById(id)
                    .orElseThrow(() -> new DataNotFoundException(
                            "Cannot find category with id: " + productDTO.getCategoryId()
                    ));

            // Update only the properties that are present in the productDTO
            if (productDTO.getName() != null) {
                existingProduct.setName(productDTO.getName());
            }

            if (productDTO.getCategoryId() != null) {
                existingProduct.setCategoryId(existingCategory);
            }

            if (productDTO.getPrice() != null) {
                existingProduct.setPrice(productDTO.getPrice());
            }

            if (productDTO.getDescription() != null) {
                existingProduct.setDescription(productDTO.getDescription());
            }

            if (productDTO.getThumbnail() != null) {
                existingProduct.setThumbnail(productDTO.getThumbnail());
            }
            return productRepository.save(existingProduct);
        }

        return null;
    }

    @Override
    public void deleteProduct(Long id) {

        Optional<Product> existingProduct = productRepository.findById(id);

        existingProduct.ifPresent(productRepository::delete);

    }

    @Override
    public boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }

    public ProductImage createProductImage(Long productId, ProductImageDTO productImageDTO)  {
        Optional<Product> existingProduct = productRepository.findById(productId);
        return existingProduct.map(product -> productImageRepository.save(ProductImage.builder()
                .productId(product)
                .imageUrl(productImageDTO.getImageUrl())
                .build())).orElse(null);

    }
}
