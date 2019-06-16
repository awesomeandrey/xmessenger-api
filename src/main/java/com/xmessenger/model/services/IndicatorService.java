package com.xmessenger.model.services;

import com.xmessenger.model.database.entities.wrappers.Indicator;
import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.database.repositories.IndicatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.rest.core.event.AfterSaveEvent;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class IndicatorService {
    private final ApplicationEventPublisher publisher;
    private final IndicatorRepository indicatorRepository;

    @Autowired
    public IndicatorService(ApplicationEventPublisher publisher, IndicatorRepository indicatorRepository) {
        this.publisher = publisher;
        this.indicatorRepository = indicatorRepository;
    }

    public void switchUserIndicator(AppUser appUser, boolean active) {
        Indicator indicator = new Indicator(appUser);
        if (active) {
            this.indicatorRepository.save(indicator);
        } else {
            this.indicatorRepository.delete(indicator);
        }
        this.publisher.publishEvent(new AfterSaveEvent(indicator));
    }

    public List<Indicator> getIndicators() {
        return (List<Indicator>) this.indicatorRepository.findAll();
    }

    public List<Indicator> getIndicators(Map<Integer, AppUser> usersMap) {
        return (List<Indicator>) this.indicatorRepository.findAll(usersMap.keySet());
    }

    public void flushIndicators() {
        this.indicatorRepository.deleteAll();
    }
}
