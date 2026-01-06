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

    Optional<Vendor> findByVendorEmail(String vendorEmail);

    List<Vendor> findByStatus(VendorStatus status);

    boolean existsByVendorEmail(String vendorEmail);

    @Query("SELECT v FROM Vendor v WHERE " +
            "LOWER(v.vendorName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(v.vendorEmail) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(v.contactPerson) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(v.specialization) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Vendor> searchVendors(@Param("query") String query);

    @Query("SELECT v FROM Vendor v JOIN v.assets a WHERE a.assetId = :assetId")
    List<Vendor> findByAssetId(@Param("assetId") String assetId);

    @Query("SELECT v FROM Vendor v WHERE :assetId MEMBER OF v.assetIds")
    List<Vendor> findByAssetIdSimple(@Param("assetId") String assetId);

    long countByStatus(VendorStatus status);
}