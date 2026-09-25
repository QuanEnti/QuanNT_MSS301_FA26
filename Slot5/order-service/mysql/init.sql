-- init.sql: Tạo database khi MySQL container khởi động lần đầu
-- Dùng chung 1 container MySQL cho cả order-service và inventory-service
CREATE DATABASE IF NOT EXISTS order_service;
CREATE DATABASE IF NOT EXISTS inventory_service;
