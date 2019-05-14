package com.xmessenger.controllers.webservices.open.websockets.events;

import com.xmessenger.model.database.entities.AppUserIndicator;
import org.springframework.context.ApplicationEvent;

public class IndicatorChangeEvent extends ApplicationEvent {
    private AppUserIndicator indicator;

    public AppUserIndicator getIndicator() {
        return indicator;
    }

    public void setIndicator(AppUserIndicator indicator) {
        this.indicator = indicator;
    }

    public IndicatorChangeEvent(Object source, AppUserIndicator indicator) {
        super(source);
        this.indicator = indicator;
    }

}