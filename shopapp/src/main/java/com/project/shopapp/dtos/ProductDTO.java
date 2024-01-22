package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String name;

    @Min(value = 0, message = "Price must be greater than or equal to 0")
    @Max(value = 10000000, message = "Price must be less than or equal to 10, 000,000")
    private Float price;

    private String thumbnail;

    private String description;

//    @JsonProperty("category_id")
    @NotNull(message = "Category Id is required")
    private Long categoryId;

    private List<MultipartFile> files;
}

/*
* {
    "name": "product 1",
    "price": 100,
    "thumbnail": "",
    "description": "San pham dep",
    "category_id": 1
}
* */