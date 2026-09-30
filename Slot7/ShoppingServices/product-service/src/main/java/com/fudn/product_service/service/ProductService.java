package com.fudn.product_service.service;

import com.fudn.product_service.dto.ProductRequest;
import com.fudn.product_service.dto.ProductResponse;
import com.fudn.product_service.model.Product;
import com.fudn.product_service.repository.IProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer — xử lý toàn bộ business logic cho Product.
 *
 * Trách nhiệm:
 *  1. Nhận DTO từ Controller
 *  2. Ánh xạ (map) Request DTO → Domain Model → lưu DB
 *  3. Ánh xạ Domain Model → Response DTO → trả về Controller
 *  4. Ghi log trạng thái xử lý
 *
 * @Service   : đăng ký bean với Spring IoC container
 * @RequiredArgsConstructor : Lombok tạo constructor inject final field
 * @Slf4j     : tạo đối tượng log (SLF4J)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final IProductRepository productRepository;

    /**
     * Tạo sản phẩm mới.
     *
     * @param productRequest dữ liệu từ client (DTO)
     * @return ProductResponse chứa thông tin product vừa tạo (kể cả id do MongoDB sinh)
     */
    public ProductResponse createProduct(ProductRequest productRequest) {
        // Map Request DTO → Domain Model dùng Builder Pattern
        Product product = Product.builder()
                .name(productRequest.name())           // Record: dùng tên method trực tiếp
                .description(productRequest.description())
                .price(productRequest.price())
                .build();

        // Lưu vào MongoDB — Spring Data tự sinh ObjectId
        Product savedProduct = productRepository.save(product);

        log.info("Product created successfully with id: {}", savedProduct.getId());

        // Map Domain Model → Response DTO
        return mapToProductResponse(savedProduct);
    }

    /**
     * Lấy toàn bộ danh sách sản phẩm.
     *
     * @return List<ProductResponse> — danh sách sản phẩm
     */
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();

        log.info("Retrieving all products, total count: {}", products.size());

        // Stream API: map từng Product → ProductResponse
        return products.stream()
                .map(this::mapToProductResponse)
                .toList();          // Java 16+ — immutable list
    }

    // ─── Private Helper ────────────────────────────────────────────────────────

    /**
     * Helper: map Product domain model → ProductResponse DTO.
     */
    private ProductResponse mapToProductResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice()
        );
    }
}
