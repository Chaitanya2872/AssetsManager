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
public interface MaintenanceRepository
        extends JpaRepository<Maintenance, String> {


    List<Maintenance>
    findByAssetIdOrderByScheduledDateDesc(
            String assetId
    );


    List<Maintenance>
    findByVendorId(
            String vendorId
    );


    List<Maintenance>
    findByStatus(
            MaintenanceStatus status
    );


    @Query("""
        SELECT m
        FROM Maintenance m
        WHERE m.scheduledDate < :currentDate
        AND m.status NOT IN (
            com.bmsedge.asset.model.MaintenanceStatus.COMPLETED,
            com.bmsedge.asset.model.MaintenanceStatus.CANCELLED
        )
    """)
    List<Maintenance> findOverdue(
            @Param("currentDate")
            LocalDate currentDate
    );


    @Query("""
        SELECT m
        FROM Maintenance m
        WHERE m.scheduledDate BETWEEN :start AND :end
        AND m.status NOT IN (
            com.bmsedge.asset.model.MaintenanceStatus.COMPLETED,
            com.bmsedge.asset.model.MaintenanceStatus.CANCELLED
        )
    """)
    List<Maintenance> findUpcoming(
            @Param("start")
            LocalDate start,

            @Param("end")
            LocalDate end
    );


    long countByStatus(
            MaintenanceStatus status
    );


    long countByAssetId(
            String assetId
    );


    @Query("""
        SELECT COALESCE(SUM(m.cost), 0.0)
        FROM Maintenance m
    """)
    Double sumCost();


    @Query("""
        SELECT m.maintenanceType, COUNT(m)
        FROM Maintenance m
        GROUP BY m.maintenanceType
    """)
    List<Object[]> countByMaintenanceType();


    @Query("""
        SELECT COUNT(m)
        FROM Maintenance m
        WHERE m.scheduledDate BETWEEN :start AND :end
        AND m.status NOT IN (
            com.bmsedge.asset.model.MaintenanceStatus.COMPLETED,
            com.bmsedge.asset.model.MaintenanceStatus.CANCELLED
        )
    """)
    long countUpcoming(
            @Param("start")
            LocalDate start,

            @Param("end")
            LocalDate end
    );
}