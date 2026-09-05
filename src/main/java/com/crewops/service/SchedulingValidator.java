package com.crewops.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

import com.crewops.entity.CrewAssignment;

import java.time.Duration;

@Component
public class SchedulingValidator {
	
	public void validateMaximumDutyHours(
	        LocalDateTime startTime,
	        LocalDateTime endTime,
	        int maximumDutyHours) {

	    if (!endTime.isAfter(startTime)) {
	        throw new IllegalArgumentException(
	                "Assignment end time must be after start time");
	    }

	    long dutyHours =
	            java.time.Duration.between(startTime, endTime).toHours();

	    if (dutyHours > maximumDutyHours) {
	        throw new IllegalArgumentException(
	                "Crew assignment exceeds the maximum duty period of "
	                + maximumDutyHours + " hours");
	    }
	}
	
	public void validateMinimumRestPeriod(
	        LocalDateTime startTime,
	        LocalDateTime endTime,
	        LocalDateTime existingStart,
	        LocalDateTime existingEnd,
	        int minimumRestHours) {

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

	    if (restBefore >= 0 && restBefore < minimumRestHours) {
	        throw new IllegalArgumentException(
	                "Crew member does not have the required minimum rest period of "
	                + minimumRestHours + " hours");
	    }

	    if (restAfter >= 0 && restAfter < minimumRestHours) {
	        throw new IllegalArgumentException(
	                "Crew member does not have the required minimum rest period of "
	                + minimumRestHours + " hours");
	    }
	}
	
	public void validateMaximumConsecutiveDutyDays(
	        List<CrewAssignment> assignments,
	        LocalDateTime assignmentStartTime,
	        int maximumConsecutiveDays) {

	    if (assignments.isEmpty()) {
	        return;
	    }

	    LocalDateTime proposedDateTime = assignmentStartTime;

	    int consecutiveDays = 1;

	    for (int i = assignments.size() - 1; i >= 0; i--) {

	        LocalDateTime existingDateTime =
	                assignments.get(i).getAssignmentStartTime();

	        long daysBetween =
	                java.time.Duration.between(
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

	            throw new IllegalArgumentException(
	                    "Crew member cannot be assigned for more than "
	                    + maximumConsecutiveDays
	                    + " consecutive duty days");
	        }
	    }
	}

}
