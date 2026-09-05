package com.crewops.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "crewops.scheduling")
public class SchedulingProperties {

    private int maximumDutyHours;
    private int minimumRestHours;
    private int maximumConsecutiveDutyDays;

    public int getMaximumDutyHours() {
        return maximumDutyHours;
    }

    public void setMaximumDutyHours(int maximumDutyHours) {
        this.maximumDutyHours = maximumDutyHours;
    }

    public int getMinimumRestHours() {
        return minimumRestHours;
    }

    public void setMinimumRestHours(int minimumRestHours) {
        this.minimumRestHours = minimumRestHours;
    }

    public int getMaximumConsecutiveDutyDays() {
        return maximumConsecutiveDutyDays;
    }

    public void setMaximumConsecutiveDutyDays(int maximumConsecutiveDutyDays) {
        this.maximumConsecutiveDutyDays = maximumConsecutiveDutyDays;
    }
}