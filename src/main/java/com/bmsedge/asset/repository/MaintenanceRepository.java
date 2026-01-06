package com.bmsedge.asset.repository;

import com.bmsedge.asset.model.Maintenance;
import com.bmsedge.asset.model.MaintenanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, String> {

    List<Maintenance> findByAssetIdOrderByScheduledDateDesc(String assetId);

    List<Maintenance> findByVendorId(String vendorId);

    List<Maintenance> findByStatus(MaintenanceStatus status);

    @Query("SELECT m FROM Maintenance m WHERE m.scheduledDate < :currentDate AND m.status != 'COMPLETED'")
    List<Maintenance> findOverdue(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT m FROM Maintenance m WHERE m.scheduledDate BETWEEN :start AND :end")
    List<Maintenance> findUpcoming(@Param("start") LocalDate start, @Param("end") LocalDate end);

    long countByStatus(MaintenanceStatus status);

    long countByAssetId(String assetId);
}