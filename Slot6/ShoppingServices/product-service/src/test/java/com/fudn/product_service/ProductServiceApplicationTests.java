package com.fudn.product_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fudn.product_service.dto.ProductRequest;
import com.fudn.product_service.repository.IProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Test dùng TestContainers — khởi động MongoDB thật trong Docker.
 *
 * @SpringBootTest        : load toàn bộ ApplicationContext
 * @Testcontainers        : kích hoạt TestContainers JUnit 5 extension
 * @AutoConfigureMockMvc  : inject MockMvc để gọi HTTP endpoint
 *
 * @Container MongoDBContainer: tự khởi động container mongo:7.0.5
 * @DynamicPropertySource     : override spring.data.mongodb.uri → trỏ vào container test
 * @BeforeEach cleanup()      : xóa DB trước mỗi test để đảm bảo test độc lập
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0.5");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    IProductRepository productRepository;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void cleanup() {
        productRepository.deleteAll();
    }

    /**
     * Test: POST /api/products → 201 Created + body có đủ field + MongoDB có 1 bản ghi.
     */
    @Test
    void shouldCreateProduct() throws Exception {
        ProductRequest productRequest = ProductRequest.builder()
                .name("Test Product")
                .description("This is a test product")
                .price(BigDecimal.valueOf(19.99))
                .build();

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.description").value("This is a test product"))
                .andExpect(jsonPath("$.price").value(19.99));

        assertThat(productRepository.findAll()).hasSize(1);
    }

    /**
     * Test: GET /api/products → 200 OK + danh sách đúng.
     */
    @Test
    void shouldGetAllProducts() throws Exception {
        // Arrange — tạo sẵn 2 sản phẩm
        ProductRequest p1 = ProductRequest.builder()
                .name("Product A").description("Desc A").price(BigDecimal.valueOf(10.00)).build();
        ProductRequest p2 = ProductRequest.builder()
                .name("Product B").description("Desc B").price(BigDecimal.valueOf(20.00)).build();

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(p1)));
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(p2)));

        // Act & Assert
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        assertThat(productRepository.findAll()).hasSize(2);
    }

    @Test
    void contextLoads() {
        // Kiểm tra ApplicationContext load thành công
    }
}
