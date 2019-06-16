package com.xmessenger.model.services;

import com.xmessenger.model.database.entities.wrappers.Indicator;
import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.database.repositories.IndicatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.rest.core.event.AfterSaveEvent;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
        Indicator indicator = new Indicator(appUser, active);
        this.indicatorRepository.save(indicator);
        this.publisher.publishEvent(new AfterSaveEvent(indicator));
    }

    public List<Indicator> getIndicators() {
        return ((List<Indicator>) this.indicatorRepository.findAll()).stream()
                .filter(Indicator::isActive)
                .collect(Collectors.toList());
    }

    public Collection<Indicator> getIndicators(Map<Integer, AppUser> usersMap) {
        Set<Indicator> defaultIndicators = usersMap.values().stream()
                .map(Indicator::new).collect(Collectors.toSet());
        Iterable<Indicator> iterable = this.indicatorRepository.findAll(usersMap.keySet());
        Set<Indicator> existingIndicators = StreamSupport.stream(iterable.spliterator(), false)
                .collect(Collectors.toSet());
        existingIndicators.addAll(defaultIndicators);
        return existingIndicators;
    }

    public void flushIndicators() {
        this.indicatorRepository.deleteAll();
    }
}
