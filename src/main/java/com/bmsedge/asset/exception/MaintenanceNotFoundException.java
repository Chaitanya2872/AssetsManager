package com.bmsedge.asset.exception;

public class MaintenanceNotFoundException extends RuntimeException {

    public MaintenanceNotFoundException(String id) {
        super("Maintenance not found with ID: " + id);
    }

    public MaintenanceNotFoundException(String field, String value) {
        super("Maintenance not found with " + field + ": " + value);
    }
}