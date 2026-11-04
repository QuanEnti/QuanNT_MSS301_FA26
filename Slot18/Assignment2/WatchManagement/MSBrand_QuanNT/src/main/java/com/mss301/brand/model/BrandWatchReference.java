package com.mss301.brand.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;

@Entity
@Table(
        name = "BrandWatchReferences",
        uniqueConstraints = @UniqueConstraint(name = "UK_BrandWatch_WatchID", columnNames = "WatchID")
)
public class BrandWatchReference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ReferenceID")
    private Long referenceId;

    @Column(name = "WatchID", nullable = false)
    private Integer watchId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BrandID", nullable = false)
    private WatchBrand brand;

    @Column(name = "RegisteredAt", nullable = false)
    private Instant registeredAt;

    protected BrandWatchReference() {
    }

    public BrandWatchReference(Integer watchId, WatchBrand brand) {
        this.watchId = watchId;
        this.brand = brand;
        this.registeredAt = Instant.now();
    }

    public Integer getWatchId() {
        return watchId;
    }

    public WatchBrand getBrand() {
        return brand;
    }

    public void changeBrand(WatchBrand brand) {
        this.brand = brand;
        this.registeredAt = Instant.now();
    }

    public Instant getRegisteredAt() {
        return registeredAt;
    }
}

