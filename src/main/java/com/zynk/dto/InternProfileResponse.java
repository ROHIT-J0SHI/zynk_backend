package com.zynk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class InternProfileResponse {
    private String name;
    private String email;
    private String role;
    private String manager;
    private LocalDate internshipStart;
    private LocalDate internshipEnd;
    private Double stipendPerMonth;

    // KYC details
    private String panNumber;
    private String aadhaarNumber;
    private String bankAccountNumber;
    private String bankIfscCode;
    private String bankName;
    private String bankBranch;

    // Contact / address
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String phoneNumber;

    // Whether KYC has been verified by HR
    private Boolean kycVerified;
}



