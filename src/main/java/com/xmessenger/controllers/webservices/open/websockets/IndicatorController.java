package com.xmessenger.controllers.webservices.open.websockets;

import com.xmessenger.controllers.webservices.open.websockets.config.WebSocketConfig;
import com.xmessenger.model.database.entities.AppUserIndicator;
import com.xmessenger.model.database.repositories.IndicatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class IndicatorController {
    private static final String API_PATH = "/indicator-change";

    private final IndicatorRepository indicatorRepository;

    @Autowired
    public IndicatorController(IndicatorRepository indicatorRepository) {
        this.indicatorRepository = indicatorRepository;
    }

    @MessageMapping(API_PATH)
    @SendTo(WebSocketConfig.TOPICS_PREFIX + API_PATH)
    public AppUserIndicator switchStatus(AppUserIndicator indicator) {
        return this.indicatorRepository.save(indicator);
    }
}