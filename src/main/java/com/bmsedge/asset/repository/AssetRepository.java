package com.bmsedge.asset.repository;

import com.bmsedge.asset.model.Asset;
import com.bmsedge.asset.model.AssetStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, String> {

    // ============================================================
    // BASIC QUERIES
    // ============================================================

    Optional<Asset> findBySerialNumber(String serialNumber);

    boolean existsBySerialNumber(String serialNumber);

    boolean existsBySerialNumberAndAssetIdNot(
            String serialNumber,
            String assetId
    );

    List<Asset> findByAssetCategory(String category);

    List<Asset> findByStatus(AssetStatus status);

    List<Asset> findByLocation(String location);

    List<Asset> findByBranch(String branch);

    List<Asset> findByManufacturer(String manufacturer);

    List<Asset> findByVendorId(String vendorId);


    // ============================================================
    // SEARCH
    // ============================================================

    @Query("""
            SELECT a
            FROM Asset a
            WHERE
                LOWER(COALESCE(a.assetName, '')) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(COALESCE(a.assetCategory, '')) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(COALESCE(a.location, '')) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(COALESCE(a.serialNumber, '')) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(COALESCE(a.modelNumber, '')) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(COALESCE(a.manufacturer, '')) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(COALESCE(a.branch, '')) LIKE LOWER(CONCAT('%', :query, '%'))
            """)
    List<Asset> searchAssets(
            @Param("query") String query
    );


    // ============================================================
    // FILTERS
    // ============================================================

    @Query("""
            SELECT a
            FROM Asset a
            WHERE
                (:name IS NULL OR :name = '' OR
                 LOWER(COALESCE(a.assetName, '')) LIKE LOWER(CONCAT('%', :name, '%')))
            AND
                (:category IS NULL OR :category = '' OR
                 a.assetCategory = :category)
            AND
                (:status IS NULL OR a.status = :status)
            AND
                (:location IS NULL OR :location = '' OR
                 a.location = :location)
            """)
    List<Asset> findByFilters(
            @Param("name") String name,
            @Param("category") String category,
            @Param("status") AssetStatus status,
            @Param("location") String location
    );


    // ============================================================
    // DLP ALERTS
    // ============================================================

    @Query("""
            SELECT a
            FROM Asset a
            WHERE a.dlpEndDate IS NOT NULL
            AND a.dlpEndDate < CURRENT_DATE
            """)
    List<Asset> findDlpExpired();


    @Query("""
            SELECT a
            FROM Asset a
            WHERE a.dlpEndDate IS NOT NULL
            AND a.dlpEndDate BETWEEN CURRENT_DATE AND :endDate
            """)
    List<Asset> findDlpExpiringBetween(
            @Param("endDate") LocalDate endDate
    );


    @Query("""
            SELECT a
            FROM Asset a
            WHERE a.dlpEndDate IS NOT NULL
            AND a.dlpEndDate BETWEEN :startDate AND :endDate
            """)
    List<Asset> findDlpExpiringBetweenDates(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );


    // ============================================================
    // WARRANTY ALERTS
    // ============================================================

    @Query("""
            SELECT a
            FROM Asset a
            WHERE a.warrantyEndDate IS NOT NULL
            AND a.warrantyEndDate < CURRENT_DATE
            """)
    List<Asset> findWarrantyExpired();


    @Query("""
            SELECT a
            FROM Asset a
            WHERE a.warrantyEndDate IS NOT NULL
            AND a.warrantyEndDate BETWEEN CURRENT_DATE AND :endDate
            """)
    List<Asset> findWarrantyExpiringBefore(
            @Param("endDate") LocalDate endDate
    );


    // ============================================================
    // VENDOR CONTRACT ALERTS
    // ============================================================

    @Query("""
            SELECT a
            FROM Asset a
            WHERE a.vendorContractEnd IS NOT NULL
            AND a.vendorContractEnd < CURRENT_DATE
            """)
    List<Asset> findContractExpired();


    @Query("""
            SELECT a
            FROM Asset a
            WHERE a.vendorContractEnd IS NOT NULL
            AND a.vendorContractEnd BETWEEN CURRENT_DATE AND :endDate
            """)
    List<Asset> findContractExpiringBefore(
            @Param("endDate") LocalDate endDate
    );


    // ============================================================
    // DISTINCT LOOKUPS
    // ============================================================

    @Query("""
            SELECT DISTINCT a.assetCategory
            FROM Asset a
            WHERE a.assetCategory IS NOT NULL
            AND a.assetCategory <> ''
            ORDER BY a.assetCategory
            """)
    List<String> findDistinctCategories();


    @Query("""
            SELECT DISTINCT a.location
            FROM Asset a
            WHERE a.location IS NOT NULL
            AND a.location <> ''
            ORDER BY a.location
            """)
    List<String> findDistinctLocations();


    @Query("""
            SELECT DISTINCT a.branch
            FROM Asset a
            WHERE a.branch IS NOT NULL
            AND a.branch <> ''
            ORDER BY a.branch
            """)
    List<String> findDistinctBranches();


    @Query("""
            SELECT DISTINCT a.manufacturer
            FROM Asset a
            WHERE a.manufacturer IS NOT NULL
            AND a.manufacturer <> ''
            ORDER BY a.manufacturer
            """)
    List<String> findDistinctManufacturers();


    // ============================================================
    // COUNT QUERIES
    // ============================================================

    long countByStatus(AssetStatus status);

    long countByAssetCategory(String category);

    long countByLocation(String location);

    long countByVendorId(String vendorId);


    // ============================================================
    // VENDOR LOOKUPS
    // ============================================================

    @Query("""
            SELECT DISTINCT a.vendorId
            FROM Asset a
            WHERE a.vendorId IS NOT NULL
            AND a.vendorId <> ''
            ORDER BY a.vendorId
            """)
    List<String> findDistinctVendorIds();


    @Query("""
            SELECT
                a.vendorId,
                a.vendorName,
                a.vendorEmail,
                a.vendorPhone,
                COUNT(a)
            FROM Asset a
            WHERE a.vendorId IS NOT NULL
            AND a.vendorId <> ''
            GROUP BY
                a.vendorId,
                a.vendorName,
                a.vendorEmail,
                a.vendorPhone
            ORDER BY a.vendorName
            """)
    List<Object[]> getVendorStatistics();


    // ============================================================
    // ASSIGNED ASSET COUNTS
    // ============================================================

    @Query("""
            SELECT COUNT(a)
            FROM Asset a
            WHERE a.assignedTo IS NOT NULL
            AND a.assignedTo <> ''
            """)
    long countAssignedAssets();


    @Query("""
            SELECT COUNT(a)
            FROM Asset a
            WHERE a.assignedTo IS NULL
            OR a.assignedTo = ''
            """)
    long countUnassignedAssets();
}