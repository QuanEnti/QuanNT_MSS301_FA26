package com.mss301.watch.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "WatchCategories")
public class WatchCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CategoryID")
    private Integer categoryId;

    @Column(name = "CategoryName", nullable = false, unique = true, length = 100)
    private String categoryName;

    @Column(name = "Description", nullable = false, columnDefinition = "nvarchar(max)")
    private String description;

    @Column(name = "MovementType", nullable = false, length = 50)
    private String movementType;

    @Column(name = "PriceRange", nullable = false, length = 100)
    private String priceRange;

    protected WatchCategory() {
    }

    public WatchCategory(
            String categoryName,
            String description,
            String movementType,
            String priceRange
    ) {
        this(null, categoryName, description, movementType, priceRange);
    }

    public WatchCategory(
            Integer categoryId,
            String categoryName,
            String description,
            String movementType,
            String priceRange
    ) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.description = description;
        this.movementType = movementType;
        this.priceRange = priceRange;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getDescription() {
        return description;
    }

    public String getMovementType() {
        return movementType;
    }

    public String getPriceRange() {
        return priceRange;
    }
}

