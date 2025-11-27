package com.zynk.controller;

import com.zynk.dto.InternOnboardingRequest;
import com.zynk.dto.InternProfileResponse;
import com.zynk.dto.InternProfileUpdateRequest;
import com.zynk.entity.InternDetails;
import com.zynk.entity.User;
import com.zynk.repository.InternDetailsRepository;
import com.zynk.service.InternDetailsService;
import com.zynk.service.JwtService;
import com.zynk.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interns")
@RequiredArgsConstructor
public class InternController {
    
    private final UserService userService;
    private final InternDetailsRepository internDetailsRepository;
    private final JwtService jwtService;
    private final InternDetailsService internDetailsService;
    
    @PostMapping("/onboard")
    public ResponseEntity<?> onboardIntern(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody InternOnboardingRequest request) {
        try {
            // Verify HR role
            String role = jwtService.extractRole(token.replace("Bearer ", ""));
            
            if (!"HR".equals(role)) {
                return ResponseEntity.status(403).body("Only HR can onboard interns");
            }
            
            // Check for unique constraints
            String requestedPan = request.getPanNumber();
            String requestedAadhaar = request.getAadhaarNumber();
            String requestedBankAccount = request.getBankAccountNumber();

            if (requestedPan != null && !requestedPan.isBlank()
                    && internDetailsRepository.existsByPanNumber(requestedPan)) {
                return ResponseEntity.badRequest().body("PAN number already exists");
            }
            
            if (requestedAadhaar != null && !requestedAadhaar.isBlank()
                    && internDetailsRepository.existsByAadhaarNumber(requestedAadhaar)) {
                return ResponseEntity.badRequest().body("Aadhaar number already exists");
            }
            
            if (requestedBankAccount != null && !requestedBankAccount.isBlank()
                    && internDetailsRepository.existsByBankAccountNumber(requestedBankAccount)) {
                return ResponseEntity.badRequest().body("Bank account number already exists");
            }
            
            // Create user
            User user = userService.createUser(
                request.getEmail(),
                request.getPassword(),
                request.getName(),
                User.UserRole.INTERN
            );
            
            // Create intern details
            InternDetails internDetails = new InternDetails();
            internDetails.setUser(user);
            internDetails.setJoiningDate(request.getJoiningDate());
            internDetails.setInternshipDurationMonths(request.getInternshipDurationMonths());
            internDetails.setStipendType(request.getStipendType());
            internDetails.setStipendAmount(request.getStipendAmount());
            // If HR has not provided PAN/Aadhaar yet, store temporary placeholders
            String panToStore = (requestedPan != null && !requestedPan.isBlank())
                    ? requestedPan
                    : "TEMP-PAN-" + System.currentTimeMillis() + "-" + user.getEmail();
            String aadhaarToStore = (requestedAadhaar != null && !requestedAadhaar.isBlank())
                    ? requestedAadhaar
                    : "TEMP-AADHAAR-" + System.currentTimeMillis() + "-" + user.getEmail();

            internDetails.setPanNumber(panToStore);
            internDetails.setAadhaarNumber(aadhaarToStore);
            internDetails.setBankAccountNumber(requestedBankAccount);
            internDetails.setBankIfscCode(request.getBankIfscCode());
            internDetails.setBankName(request.getBankName());
            internDetails.setBankBranch(request.getBankBranch());
            internDetails.setAddress(request.getAddress());
            internDetails.setCity(request.getCity());
            internDetails.setState(request.getState());
            internDetails.setPincode(request.getPincode());
            internDetails.setPhoneNumber(request.getPhoneNumber());
            
            internDetailsRepository.save(internDetails);
            
            return ResponseEntity.ok("Intern onboarded successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<InternDetails>> getAllInterns() {
        List<InternDetails> interns = internDetailsRepository.findAll();
        return ResponseEntity.ok(interns);
    }
    
    /**
     * Get the profile details for the currently logged-in intern.
     */
    @GetMapping("/me")
    public ResponseEntity<InternProfileResponse> getMyProfile(
            @RequestHeader("Authorization") String token) {
        Long userId = jwtService.extractUserId(token.replace("Bearer ", ""));
        InternDetails internDetails = internDetailsService.getInternDetailsByUserId(userId);
        User user = internDetails.getUser();

        InternProfileResponse response = new InternProfileResponse(
                user.getName(),
                user.getEmail(),
                "INTERN",
                null,
                internDetails.getJoiningDate(),
                internDetails.getInternshipEndDate(),
                internDetails.getStipendAmount(),
                internDetails.getPanNumber(),
                internDetails.getAadhaarNumber(),
                internDetails.getBankAccountNumber(),
                internDetails.getBankIfscCode(),
                internDetails.getBankName(),
                internDetails.getBankBranch(),
                internDetails.getAddress(),
                internDetails.getCity(),
                internDetails.getState(),
                internDetails.getPincode(),
                internDetails.getPhoneNumber(),
                Boolean.TRUE.equals(internDetails.getKycVerified())
        );

        return ResponseEntity.ok(response);
    }
    
    /**
     * Allow intern to update their own KYC and contact details.
     */
    @PutMapping("/me")
    public ResponseEntity<InternProfileResponse> updateMyProfile(
            @RequestHeader("Authorization") String token,
            @RequestBody InternProfileUpdateRequest request) {
        Long userId = jwtService.extractUserId(token.replace("Bearer ", ""));
        InternDetails internDetails = internDetailsService.getInternDetailsByUserId(userId);
        
        boolean kycVerified = Boolean.TRUE.equals(internDetails.getKycVerified());
        
        // If KYC not yet verified, intern can update PAN/Aadhaar/bank details as well
        if (!kycVerified) {
            if (request.getPanNumber() != null && !request.getPanNumber().isBlank()) {
                String newPan = request.getPanNumber();
                if (!newPan.equals(internDetails.getPanNumber())
                        && internDetailsRepository.existsByPanNumber(newPan)) {
                    return ResponseEntity.badRequest().body(null);
                }
                internDetails.setPanNumber(newPan);
            }
            if (request.getAadhaarNumber() != null && !request.getAadhaarNumber().isBlank()) {
                String newAadhaar = request.getAadhaarNumber();
                if (!newAadhaar.equals(internDetails.getAadhaarNumber())
                        && internDetailsRepository.existsByAadhaarNumber(newAadhaar)) {
                    return ResponseEntity.badRequest().body(null);
                }
                internDetails.setAadhaarNumber(newAadhaar);
            }
            if (request.getBankAccountNumber() != null && !request.getBankAccountNumber().isBlank()) {
                String newAccount = request.getBankAccountNumber();
                String existingAccount = internDetails.getBankAccountNumber();
                if (existingAccount == null || !existingAccount.equals(newAccount)) {
                    if (internDetailsRepository.existsByBankAccountNumber(newAccount)) {
                        return ResponseEntity.badRequest().body(null);
                    }
                }
                internDetails.setBankAccountNumber(newAccount);
            }
            if (request.getBankIfscCode() != null) {
                internDetails.setBankIfscCode(request.getBankIfscCode());
            }
            if (request.getBankName() != null) {
                internDetails.setBankName(request.getBankName());
            }
            if (request.getBankBranch() != null) {
                internDetails.setBankBranch(request.getBankBranch());
            }
        }
        
        // Address and phone are always editable
        if (request.getAddress() != null) {
            internDetails.setAddress(request.getAddress());
        }
        if (request.getCity() != null) {
            internDetails.setCity(request.getCity());
        }
        if (request.getState() != null) {
            internDetails.setState(request.getState());
        }
        if (request.getPincode() != null) {
            internDetails.setPincode(request.getPincode());
        }
        if (request.getPhoneNumber() != null) {
            internDetails.setPhoneNumber(request.getPhoneNumber());
        }
        
        internDetailsRepository.save(internDetails);
        
        return getMyProfile(token);
    }
    
    /**
     * HR endpoint to mark an intern's KYC as verified.
     */
    @PutMapping("/{internId}/verify-kyc")
    public ResponseEntity<?> verifyKyc(
            @RequestHeader("Authorization") String token,
            @PathVariable Long internId) {
        String role = jwtService.extractRole(token.replace("Bearer ", ""));
        if (!"HR".equals(role)) {
            return ResponseEntity.status(403).body("Only HR can verify KYC");
        }
        InternDetails internDetails = internDetailsRepository.findById(internId)
                .orElseThrow(() -> new RuntimeException("Intern not found"));
        internDetails.setKycVerified(true);
        internDetailsRepository.save(internDetails);
        return ResponseEntity.ok("KYC verified for intern ID " + internId);
    }
}

