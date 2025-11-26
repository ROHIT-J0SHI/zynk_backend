package com.zynk.controller;

import com.zynk.dto.InternOnboardingRequest;
import com.zynk.entity.InternDetails;
import com.zynk.entity.User;
import com.zynk.repository.InternDetailsRepository;
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
            if (internDetailsRepository.existsByPanNumber(request.getPanNumber())) {
                return ResponseEntity.badRequest().body("PAN number already exists");
            }
            
            if (internDetailsRepository.existsByAadhaarNumber(request.getAadhaarNumber())) {
                return ResponseEntity.badRequest().body("Aadhaar number already exists");
            }
            
            if (request.getBankAccountNumber() != null && 
                !request.getBankAccountNumber().isEmpty() &&
                internDetailsRepository.existsByBankAccountNumber(request.getBankAccountNumber())) {
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
            internDetails.setPanNumber(request.getPanNumber());
            internDetails.setAadhaarNumber(request.getAadhaarNumber());
            internDetails.setBankAccountNumber(request.getBankAccountNumber());
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
}

