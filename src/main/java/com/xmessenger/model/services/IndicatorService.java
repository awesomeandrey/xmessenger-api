package com.xmessenger.model.services;

import com.xmessenger.model.database.entities.AppUserIndicator;
import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.database.repositories.IndicatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.rest.core.event.AfterSaveEvent;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class IndicatorService {
    private final ApplicationEventPublisher publisher;
    private final IndicatorRepository indicatorRepository;

    @Autowired
    public IndicatorService(ApplicationEventPublisher publisher, IndicatorRepository indicatorRepository) {
        this.publisher = publisher;
        this.indicatorRepository = indicatorRepository;
    }

    public void switchUserIndicator(AppUser appUser, boolean loggedIn) {
        AppUserIndicator indicator = new AppUserIndicator(appUser);
        indicator.setLoggedIn(loggedIn);
        this.indicatorRepository.save(indicator);
        this.publisher.publishEvent(new AfterSaveEvent(indicator));
    }

    public List<AppUserIndicator> getIndicators(Set<Integer> userIds) {
        return (List<AppUserIndicator>) this.indicatorRepository.findAll(userIds);
    }

    public void flushIndicators() {
        this.indicatorRepository.deleteAll();
    }
}
