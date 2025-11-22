package com.zynk.controller;

import com.zynk.dto.InternOnboardingRequest;
import com.zynk.entity.InternDetails;
import com.zynk.entity.User;
import com.zynk.repository.InternDetailsRepository;
import com.zynk.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interns")
@RequiredArgsConstructor
public class InternController {
    
    private final UserService userService;
    private final InternDetailsRepository internDetailsRepository;
    
    @PostMapping("/onboard")
    public ResponseEntity<?> onboardIntern(@Valid @RequestBody InternOnboardingRequest request) {
        try {
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
}

