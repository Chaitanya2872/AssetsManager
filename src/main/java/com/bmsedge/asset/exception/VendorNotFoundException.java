package com.bmsedge.asset.exception;

public class VendorNotFoundException extends RuntimeException {

    public VendorNotFoundException(String id) {
        super("Vendor not found with ID: " + id);
    }

    public VendorNotFoundException(String field, String value) {
        super("Vendor not found with " + field + ": " + value);
    }
}