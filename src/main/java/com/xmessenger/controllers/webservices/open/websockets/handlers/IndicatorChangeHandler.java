package com.xmessenger.controllers.webservices.open.websockets.handlers;

import com.xmessenger.controllers.webservices.open.websockets.IndicatorController;
import com.xmessenger.controllers.webservices.open.websockets.events.IndicatorChangeEvent;
import com.xmessenger.model.database.entities.AppUserIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import static com.xmessenger.controllers.webservices.open.websockets.config.WebSocketConfig.TOPICS_PREFIX;

@Component
public class IndicatorChangeHandler implements ApplicationListener<IndicatorChangeEvent> {
    private final SimpMessagingTemplate websocket;

    @Autowired
    public IndicatorChangeHandler(SimpMessagingTemplate websocket) {
        this.websocket = websocket;
    }

    @Override
    public void onApplicationEvent(IndicatorChangeEvent indicatorChangeEvent) {
        AppUserIndicator indicator = indicatorChangeEvent.getIndicator();
        this.websocket.convertAndSend(
                TOPICS_PREFIX + IndicatorController.API_PATH, indicator
        );
    }
}
