package com.xmessenger.model.services;

import com.xmessenger.model.database.entities.AppUserIndicator;
import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.database.repositories.IndicatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IndicatorService {
    private final IndicatorRepository indicatorRepository;

    @Autowired
    public IndicatorService(IndicatorRepository indicatorRepository) {
        this.indicatorRepository = indicatorRepository;
    }

    public void switchUserIndicator(AppUser appUser, boolean loggedIn) {
        AppUserIndicator indicator = new AppUserIndicator(appUser);
        indicator.setLoggedIn(loggedIn);
        this.indicatorRepository.save(indicator);
    }

    public void flushIndicators() {
        this.indicatorRepository.deleteAll();
    }
}
