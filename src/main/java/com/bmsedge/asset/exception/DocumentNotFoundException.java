package com.bmsedge.asset.exception;

public class DocumentNotFoundException extends RuntimeException {

    public DocumentNotFoundException(String id) {
        super("Document not found with ID: " + id);
    }

    public DocumentNotFoundException(String field, String value) {
        super("Document not found with " + field + ": " + value);
    }
}