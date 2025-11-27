package com.zynk.dto;

import com.zynk.entity.InternDetails;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class InternOnboardingRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotNull(message = "Joining date is required")
    private LocalDate joiningDate;
    
    @NotNull(message = "Internship duration is required")
    @Min(value = 3, message = "Duration must be 3 or 6 months")
    @Max(value = 6, message = "Duration must be 3 or 6 months")
    private Integer internshipDurationMonths;
    
    @NotNull(message = "Stipend type is required")
    private InternDetails.StipendType stipendType;
    
    @NotNull(message = "Stipend amount is required")
    @Positive(message = "Stipend amount must be positive")
    private Double stipendAmount;
    
    // PAN and Aadhaar are now optional at onboarding.
    // HR can create an intern without KYC; the intern will later provide these
    // details from their own portal, and HR can verify them.
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

