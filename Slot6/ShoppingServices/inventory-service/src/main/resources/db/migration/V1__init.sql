-- V1__init.sql: Tạo bảng t_inventory
-- Flyway chạy file này đầu tiên khi khởi động app lần đầu.

CREATE TABLE `t_inventory`
(
    `id`       bigint(20)   NOT NULL AUTO_INCREMENT,
    `sku_code` varchar(255) DEFAULT NULL,
    `quantity` int(11)      DEFAULT NULL,
    PRIMARY KEY (`id`)
);
