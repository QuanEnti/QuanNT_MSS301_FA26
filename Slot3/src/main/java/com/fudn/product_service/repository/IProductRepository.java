package com.fudn.product_service.repository;

import com.fudn.product_service.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository layer — tương tác với MongoDB.
 * Kế thừa MongoRepository để có sẵn: save(), findAll(), findById(), deleteAll()…
 * Spring Data MongoDB tự tạo implementation, không cần viết code thủ công.
 *
 * Generic parameters:
 *  - Product : kiểu thực thể quản lý
 *  - String  : kiểu @Id trong Product
 */
public interface IProductRepository extends MongoRepository<Product, String> {
    // Thêm custom query methods tại đây nếu cần
    // Ví dụ: List<Product> findByNameContainingIgnoreCase(String name);
}
