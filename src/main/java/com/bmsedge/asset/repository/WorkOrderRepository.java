package com.bmsedge.asset.repository;

import com.bmsedge.asset.model.WorkOrder;
import com.bmsedge.asset.model.WorkOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, String> {

    Optional<WorkOrder> findByWorkOrderNumber(String workOrderNumber);

    List<WorkOrder> findByAssetIdOrderByCreatedAtDesc(String assetId);

    List<WorkOrder> findByVendorId(String vendorId);

    List<WorkOrder> findByStatus(WorkOrderStatus status);

    List<WorkOrder> findByAssignedTo(String assignedTo);

    @Query("SELECT w FROM WorkOrder w WHERE w.status NOT IN ('COMPLETED', 'CANCELLED', 'CLOSED')")
    List<WorkOrder> findActive();

    @Query("SELECT w FROM WorkOrder w WHERE w.dueDate < :currentDate AND w.status NOT IN ('COMPLETED', 'CANCELLED', 'CLOSED')")
    List<WorkOrder> findOverdue(@Param("currentDate") LocalDate currentDate);

    long countByStatus(WorkOrderStatus status);

    long countByAssetId(String assetId);

    long countByVendorId(String vendorId);
}