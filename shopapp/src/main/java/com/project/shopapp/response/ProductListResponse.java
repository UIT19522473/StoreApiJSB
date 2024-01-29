package com.project.shopapp.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ProductListResponse {
    private List<ProductResponse> products;
    private int totalPages;
}
