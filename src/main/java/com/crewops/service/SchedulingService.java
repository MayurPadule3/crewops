package com.crewops.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.crewops.entity.CrewAssignment;
import com.crewops.repository.CrewAssignmentRepository;

@Service
public class SchedulingService {

    private final CrewAssignmentRepository crewAssignmentRepository;

    public SchedulingService(
            CrewAssignmentRepository crewAssignmentRepository) {

        this.crewAssignmentRepository = crewAssignmentRepository;
    }

    // Rule: Check whether crew member has an overlapping assignment
    public boolean isCrewAvailable(
            Long crewId,
            LocalDateTime startTime,
            LocalDateTime endTime) {

        List<CrewAssignment> assignments =
                crewAssignmentRepository.findByCrewId(crewId);

        for (CrewAssignment assignment : assignments) {

            boolean overlap =
                    startTime.isBefore(
                            assignment.getAssignmentEndTime())
                    && endTime.isAfter(
                            assignment.getAssignmentStartTime());

            if (overlap) {
                return false;
            }
        }

        return true;
    }

    // Rule: Check whether crew member has minimum rest period
    public boolean hasMinimumRestPeriod(
            Long crewId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            int minimumRestHours) {

        List<CrewAssignment> assignments =
                crewAssignmentRepository.findByCrewId(crewId);

        for (CrewAssignment assignment : assignments) {

            LocalDateTime existingStart =
                    assignment.getAssignmentStartTime();

            LocalDateTime existingEnd =
                    assignment.getAssignmentEndTime();

            long restBefore =
                    Duration.between(
                            existingEnd,
                            startTime)
                    .toHours();

            long restAfter =
                    Duration.between(
                            endTime,
                            existingStart)
                    .toHours();

            if (restBefore >= 0
                    && restBefore < minimumRestHours) {

                return false;
            }

            if (restAfter >= 0
                    && restAfter < minimumRestHours) {

                return false;
            }
        }

        return true;
    }
    
    public boolean isWithinMaximumDutyHours(
            LocalDateTime startTime,
            LocalDateTime endTime,
            int maximumDutyHours) {

        long dutyHours =
                Duration.between(startTime, endTime).toHours();

        return dutyHours <= maximumDutyHours;
    }
    
    public boolean isWithinMaximumConsecutiveDutyDays(
            Long crewId,
            LocalDateTime assignmentStartTime,
            int maximumConsecutiveDays) {

        List<CrewAssignment> assignments =
                crewAssignmentRepository
                        .findByCrewIdOrderByAssignmentStartTimeAsc(crewId);

        if (assignments.isEmpty()) {
            return true;
        }

        LocalDateTime proposedDateTime = assignmentStartTime;

        int consecutiveDays = 1;

        for (int i = assignments.size() - 1; i >= 0; i--) {

            LocalDateTime existingDateTime =
                    assignments.get(i).getAssignmentStartTime();

            long daysBetween =
                    Duration.between(
                            existingDateTime,
                            proposedDateTime)
                            .toDays();

            if (daysBetween == 1) {

                consecutiveDays++;

                proposedDateTime = existingDateTime;

            } else if (daysBetween == 0) {

                proposedDateTime = existingDateTime;

            } else {

                break;
            }

            if (consecutiveDays > maximumConsecutiveDays) {
                return false;
            }
        }

        return true;
    }
}