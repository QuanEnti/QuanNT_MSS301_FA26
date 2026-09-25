package com.fudn.product_service.controller;

import com.fudn.product_service.dto.ProductRequest;
import com.fudn.product_service.dto.ProductResponse;
import com.fudn.product_service.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // ==========================================================
    // ĐÃ ĐƯỢC IMPLEMENT — đọc kỹ làm mẫu
    // ==========================================================

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(@RequestBody ProductRequest productRequest) {
        return productService.createProduct(productRequest);
    }

    @GetMapping
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }

    // ==========================================================
    // TODO 3 — Endpoint UPDATE sản phẩm
    // ----------------------------------------------------------
    // YÊU CẦU:
    //   - HTTP method : PUT
    //   - URL path   : /api/products/{id}
    //   - Path var   : id  (String)
    //   - Body       : ProductRequest
    //   - Return     : ProductResponse (dữ liệu sau khi update)
    //   - Status     : 200 OK (mặc định của Spring khi return không-void)
    //   - Khi service throw ProductNotFoundException -> 404
    //     (xử lý bằng GlobalExceptionHandler ở TODO 5)
    //
    // GỢI Ý ANNOTATION:
    //   @PutMapping("/{id}")
    //   public ProductResponse updateProduct(
    //          @PathVariable String id,
    //          @RequestBody ProductRequest productRequest) { ... }
    // ==========================================================
    // TODO 3 — PUT /api/products/{id} — Update sản phẩm
    @PutMapping("/{id}")
    public ProductResponse updateProduct(@PathVariable String id,
                                         @RequestBody ProductRequest productRequest) {
        return productService.updateProduct(id, productRequest);
        // Status 200 OK — Spring mặc định khi method return non-void
        // Khi service throw ProductNotFoundException → GlobalExceptionHandler bắt → 404
    }

    // TODO 4 — DELETE /api/products/{id} — Xoá sản phẩm
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)    // 204 No Content khi xoá thành công
    public void deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        // Khi service throw ProductNotFoundException → GlobalExceptionHandler bắt → 404
    }
}
