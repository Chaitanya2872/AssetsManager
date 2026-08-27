package com.bmsedge.asset.repository;

import com.bmsedge.asset.model.Vendor;
import com.bmsedge.asset.model.VendorStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, String> {

    // ============================================================
    // BASIC QUERIES
    // ============================================================

    Optional<Vendor> findByVendorEmail(String vendorEmail);

    List<Vendor> findByStatus(VendorStatus status);

    boolean existsByVendorEmail(String vendorEmail);


    // ============================================================
    // SEARCH
    // ============================================================

    @Query("""
            SELECT v
            FROM Vendor v
            WHERE
                LOWER(COALESCE(v.vendorName, '')) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(COALESCE(v.vendorEmail, '')) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(COALESCE(v.contactPerson, '')) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(COALESCE(v.specialization, '')) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(COALESCE(v.vendorPhone, '')) LIKE LOWER(CONCAT('%', :query, '%'))
            ORDER BY v.vendorName
            """)
    List<Vendor> searchVendors(
            @Param("query") String query
    );


    // ============================================================
    // ASSET RELATIONSHIP
    // ============================================================

    @Query("""
            SELECT DISTINCT v
            FROM Vendor v
            JOIN v.assets a
            WHERE a.assetId = :assetId
            """)
    List<Vendor> findByAssetId(
            @Param("assetId") String assetId
    );


    @Query("""
            SELECT DISTINCT v
            FROM Vendor v
            JOIN v.assetIds ids
            WHERE ids = :assetId
            """)
    List<Vendor> findByAssetIdSimple(
            @Param("assetId") String assetId
    );


    // ============================================================
    // COUNTS
    // ============================================================

    long countByStatus(VendorStatus status);


    // ============================================================
    // EMAIL VALIDATION
    // ============================================================

    boolean existsByVendorEmailIgnoreCase(String vendorEmail);


    boolean existsByVendorEmailAndVendorIdNot(
            String vendorEmail,
            String vendorId
    );


    // ============================================================
    // ACTIVE VENDORS
    // ============================================================

    @Query("""
            SELECT v
            FROM Vendor v
            WHERE v.status = com.bmsedge.asset.model.VendorStatus.ACTIVE
            ORDER BY v.vendorName
            """)
    List<Vendor> findActiveVendors();


    // ============================================================
    // VENDORS WITHOUT ASSETS
    // ============================================================

    @Query("""
            SELECT v
            FROM Vendor v
            WHERE v.assets IS EMPTY
            ORDER BY v.vendorName
            """)
    List<Vendor> findVendorsWithoutAssets();
}