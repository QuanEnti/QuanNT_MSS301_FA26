package com.fudn.product_service.controller;

import com.fudn.product_service.dto.ProductRequest;
import com.fudn.product_service.dto.ProductResponse;
import com.fudn.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller — nhận request HTTP, chuyển tiếp đến Service, trả response.
 * Không chứa business logic.
 *
 * Base URL: /api/products
 *
 * Endpoints:
 *   POST   /api/products  → createProduct  (HTTP 201 Created)
 *   GET    /api/products  → getAllProducts  (HTTP 200 OK)
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * Tạo sản phẩm mới.
     * POST /api/products
     *
     * @param productRequest JSON body từ client
     * @return ProductResponse với HTTP 201 Created
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(@RequestBody ProductRequest productRequest) {
        return productService.createProduct(productRequest);
    }

    /**
     * Lấy toàn bộ danh sách sản phẩm.
     * GET /api/products
     *
     * @return List<ProductResponse> với HTTP 200 OK
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }
}
