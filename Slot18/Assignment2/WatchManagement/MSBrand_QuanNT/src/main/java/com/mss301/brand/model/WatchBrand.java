package com.mss301.brand.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "WatchBrands")
public class WatchBrand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BrandID")
    private Integer brandId;

    @Column(name = "BrandName", nullable = false, unique = true, length = 200)
    private String brandName;

    @Column(name = "CountryOfOrigin", nullable = false, length = 50)
    private String countryOfOrigin;

    protected WatchBrand() {
    }

    public WatchBrand(String brandName, String countryOfOrigin) {
        this(null, brandName, countryOfOrigin);
    }

    public WatchBrand(Integer brandId, String brandName, String countryOfOrigin) {
        this.brandId = brandId;
        this.brandName = brandName;
        this.countryOfOrigin = countryOfOrigin;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }
}

