package com.mss301.watch.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Watches")
public class Watch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WatchID")
    private Integer watchId;

    @Column(name = "Name", nullable = false, length = 255)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CategoryID", nullable = false)
    private WatchCategory category;

    @Column(name = "BrandID", nullable = false)
    private Integer brandId;

    @Column(name = "CaseMaterial", nullable = false, length = 100)
    private String caseMaterial;

    @Column(name = "StrapMaterial", nullable = false, length = 100)
    private String strapMaterial;

    @Column(name = "WaterResistance", nullable = false, length = 50)
    private String waterResistance;

    @Column(name = "Price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "ReleaseDate", nullable = false)
    private LocalDate releaseDate;

    @Column(name = "Stock", nullable = false)
    private Integer stock;

    protected Watch() {
    }

    public Watch(
            String name,
            WatchCategory category,
            Integer brandId,
            String caseMaterial,
            String strapMaterial,
            String waterResistance,
            BigDecimal price,
            LocalDate releaseDate,
            Integer stock
    ) {
        update(
                name,
                category,
                brandId,
                caseMaterial,
                strapMaterial,
                waterResistance,
                price,
                releaseDate,
                stock
        );
    }

    public void update(
            String name,
            WatchCategory category,
            Integer brandId,
            String caseMaterial,
            String strapMaterial,
            String waterResistance,
            BigDecimal price,
            LocalDate releaseDate,
            Integer stock
    ) {
        this.name = name;
        this.category = category;
        this.brandId = brandId;
        this.caseMaterial = caseMaterial;
        this.strapMaterial = strapMaterial;
        this.waterResistance = waterResistance;
        this.price = price;
        this.releaseDate = releaseDate;
        this.stock = stock;
    }

    public Integer getWatchId() {
        return watchId;
    }

    public String getName() {
        return name;
    }

    public WatchCategory getCategory() {
        return category;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public String getCaseMaterial() {
        return caseMaterial;
    }

    public String getStrapMaterial() {
        return strapMaterial;
    }

    public String getWaterResistance() {
        return waterResistance;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public Integer getStock() {
        return stock;
    }
}

