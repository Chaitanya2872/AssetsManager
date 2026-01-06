package com.bmsedge.asset.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class VendorCreateRequest {

    @NotBlank(message = "Vendor name is required")
    private String vendorName;

    @NotBlank(message = "Vendor email is required")
    @Email(message = "Invalid email format")
    private String vendorEmail;

    private String vendorPhone;
    private String vendorAddress;
    private String contactPerson;
    private String website;
    private String specialization;
    private String notes;

    // Constructors
    public VendorCreateRequest() {
    }

    public VendorCreateRequest(String vendorName, String vendorEmail) {
        this.vendorName = vendorName;
        this.vendorEmail = vendorEmail;
    }

    // Getters and Setters
    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getVendorEmail() {
        return vendorEmail;
    }

    public void setVendorEmail(String vendorEmail) {
        this.vendorEmail = vendorEmail;
    }

    public String getVendorPhone() {
        return vendorPhone;
    }

    public void setVendorPhone(String vendorPhone) {
        this.vendorPhone = vendorPhone;
    }

    public String getVendorAddress() {
        return vendorAddress;
    }

    public void setVendorAddress(String vendorAddress) {
        this.vendorAddress = vendorAddress;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}