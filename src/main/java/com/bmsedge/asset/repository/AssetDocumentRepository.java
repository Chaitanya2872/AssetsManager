package com.bmsedge.asset.repository;

import com.bmsedge.asset.model.AssetDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetDocumentRepository extends JpaRepository<AssetDocument, String> {

    List<AssetDocument> findByAssetIdOrderByCreatedAtDesc(String assetId);

    List<AssetDocument> findByVendorId(String vendorId);

    List<AssetDocument> findByCategory(String category);

    @Query("SELECT d FROM AssetDocument d WHERE " +
            "LOWER(d.documentName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(d.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(d.tags) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<AssetDocument> searchDocuments(@Param("keyword") String keyword);

    long countByAssetId(String assetId);

    long countByVendorId(String vendorId);

    long countByCategory(String category);
}