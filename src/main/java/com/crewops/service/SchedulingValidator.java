package com.crewops.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

import com.crewops.entity.CrewAssignment;

@Component
public class SchedulingValidator {

    public void validateAssignmentTime(
            LocalDateTime startTime,
            LocalDateTime endTime) {

        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException(
                    "Assignment end time must be after start time");
        }
    }

    public void validateMaximumDutyHours(
            LocalDateTime startTime,
            LocalDateTime endTime,
            int maximumDutyHours) {

        long dutyHours =
                Duration.between(startTime, endTime)
                        .toHours();

        if (dutyHours > maximumDutyHours) {
            throw new IllegalArgumentException(
                    "Crew assignment exceeds the maximum duty period of "
                    + maximumDutyHours + " hours");
        }
    }

    public void validateMinimumRestPeriod(
            List<CrewAssignment> assignments,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Long excludedAssignmentId,
            int minimumRestHours) {

        for (CrewAssignment assignment : assignments) {

            if (excludedAssignmentId != null
                    && excludedAssignmentId.equals(
                            assignment.getId())) {
                continue;
            }

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

                throw new IllegalArgumentException(
                        "Crew member does not have the required minimum rest period of "
                        + minimumRestHours + " hours");
            }

            if (restAfter >= 0
                    && restAfter < minimumRestHours) {

                throw new IllegalArgumentException(
                        "Crew member does not have the required minimum rest period of "
                        + minimumRestHours + " hours");
            }
        }
    }

    public void validateNoOverlap(
            List<CrewAssignment> assignments,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Long excludedAssignmentId) {

        for (CrewAssignment assignment : assignments) {

            if (excludedAssignmentId != null
                    && excludedAssignmentId.equals(
                            assignment.getId())) {
                continue;
            }

            boolean overlap =
                    startTime.isBefore(
                            assignment.getAssignmentEndTime())
                    && endTime.isAfter(
                            assignment.getAssignmentStartTime());

            if (overlap) {
                throw new IllegalArgumentException(
                        "Crew member already has an overlapping assignment");
            }
        }
    }

    public void validateMaximumConsecutiveDutyDays(
            List<CrewAssignment> assignments,
            LocalDateTime assignmentStartTime,
            Long excludedAssignmentId,
            int maximumConsecutiveDays) {

        List<CrewAssignment> filteredAssignments =
                assignments.stream()
                        .filter(assignment ->
                                excludedAssignmentId == null
                                || !excludedAssignmentId.equals(
                                        assignment.getId()))
                        .sorted((a, b) ->
                                a.getAssignmentStartTime()
                                        .compareTo(
                                                b.getAssignmentStartTime()))
                        .toList();

        if (filteredAssignments.isEmpty()) {
            return;
        }

        LocalDateTime proposedDateTime =
                assignmentStartTime;

        int consecutiveDays = 1;

        for (int i = filteredAssignments.size() - 1;
                i >= 0;
                i--) {

            LocalDateTime existingDateTime =
                    filteredAssignments.get(i)
                            .getAssignmentStartTime();

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

            } else if (daysBetween > 1) {

                break;
            }

            if (consecutiveDays > maximumConsecutiveDays) {

                throw new IllegalArgumentException(
                        "Crew member cannot be assigned for more than "
                        + maximumConsecutiveDays
                        + " consecutive duty days");
            }
        }
    }
}