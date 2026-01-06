package com.bmsedge.asset.exception;

public class WorkOrderNotFoundException extends RuntimeException {

    public WorkOrderNotFoundException(String id) {
        super("Work order not found with ID: " + id);
    }

    public WorkOrderNotFoundException(String field, String value) {
        super("Work order not found with " + field + ": " + value);
    }
}