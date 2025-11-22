package com.zynk.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "intern_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InternDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    
    @Column(nullable = false)
    private LocalDate joiningDate;
    
    @Column(nullable = false)
    private Integer internshipDurationMonths; // 3 or 6
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StipendType stipendType; // MONTHLY or DAILY
    
    @Column(nullable = false)
    private Double stipendAmount;
    
    // Personal Information
    @Column(nullable = false)
    private String panNumber;
    
    @Column(nullable = false)
    private String aadhaarNumber;
    
    private String bankAccountNumber;
    private String bankIfscCode;
    private String bankName;
    private String bankBranch;
    
    // Address
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String phoneNumber;
    
    // Signature file path (stored on server)
    private String signatureFilePath;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum StipendType {
        MONTHLY, DAILY
    }
    
    // Helper method to calculate internship end date
    public LocalDate getInternshipEndDate() {
        return joiningDate.plusMonths(internshipDurationMonths);
    }
}

