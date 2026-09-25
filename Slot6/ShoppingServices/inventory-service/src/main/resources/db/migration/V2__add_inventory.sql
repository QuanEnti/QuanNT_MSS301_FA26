-- V2__add_inventory.sql: Thêm dữ liệu mẫu 4 SKU
-- Flyway chạy sau V1, mỗi SKU có quantity = 100.

INSERT INTO t_inventory (quantity, sku_code)
VALUES (100, 'iphone_15'),
       (100, 'pixel_8'),
       (100, 'galaxy_24'),
       (100, 'oneplus_12');
