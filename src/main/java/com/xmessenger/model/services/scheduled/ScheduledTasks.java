package com.xmessenger.model.services.scheduled;

import com.xmessenger.model.services.core.IndicatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {
    private final IndicatorService indicatorService;

    @Autowired
    public ScheduledTasks(IndicatorService indicatorService) {
        this.indicatorService = indicatorService;
    }

    @Scheduled(fixedRate = 1000 * 60 * 10)
    public void resetIndicators() {
        this.indicatorService.flushIndicators();
    }
}
