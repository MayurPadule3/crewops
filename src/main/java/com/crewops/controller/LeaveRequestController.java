package com.crewops.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crewops.dto.LeaveRequestRequest;
import com.crewops.dto.LeaveRequestResponse;
import com.crewops.service.LeaveRequestService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/leave-requests")
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    public LeaveRequestController(LeaveRequestService leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }

    @PostMapping
    public LeaveRequestResponse createLeaveRequest(
            @Valid @RequestBody LeaveRequestRequest leaveRequestRequest) {

        return leaveRequestService.createLeaveRequest(leaveRequestRequest);
    }

    @GetMapping
    public List<LeaveRequestResponse> getAllLeaveRequests() {

        return leaveRequestService.getAllLeaveRequests();
    }

    @GetMapping("/{id}")
    public LeaveRequestResponse getLeaveRequestById(
            @PathVariable Long id) {

        return leaveRequestService.getLeaveRequestById(id);
    }

    @PutMapping("/{id}")
    public LeaveRequestResponse updateLeaveRequest(
            @PathVariable Long id,
            @Valid @RequestBody LeaveRequestRequest leaveRequestRequest) {

        return leaveRequestService.updateLeaveRequest(
                id, leaveRequestRequest);
    }

    @DeleteMapping("/{id}")
    public void deleteLeaveRequest(@PathVariable Long id) {

        leaveRequestService.deleteLeaveRequest(id);
    }
}
