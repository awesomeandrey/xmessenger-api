package com.xmessenger.model.database.config;

import com.xmessenger.model.database.repositories.IndicatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {
    private final IndicatorRepository indicatorRepository;

    @Autowired
    public ScheduledTasks(IndicatorRepository indicatorRepository) {
        this.indicatorRepository = indicatorRepository;
    }

    @Scheduled(fixedRate = 1000 * 60 * 10)
    public void reset() {
        this.indicatorRepository.deleteAll();
    }
}
