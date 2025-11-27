package com.zynk.dto;

import lombok.Data;

/**
 * Fields that an intern is allowed to update from their own profile screen.
 * HR-owned fields like name/email/joiningDate/stipend are not exposed here.
 */
@Data
public class InternProfileUpdateRequest {
    private String panNumber;
    private String aadhaarNumber;
    private String bankAccountNumber;
    private String bankIfscCode;
    private String bankName;
    private String bankBranch;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String phoneNumber;
}


