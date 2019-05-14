package com.xmessenger.controllers.webservices.open.websockets.handlers;

import com.xmessenger.model.database.entities.AppUserIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import static com.xmessenger.controllers.webservices.open.websockets.config.WebSocketConfig.TOPICS_PREFIX;

@Component
@RepositoryEventHandler(AppUserIndicator.class)
public class IndicatorChangeHandler {
    private final SimpMessagingTemplate websocket;

    @Autowired
    public IndicatorChangeHandler(SimpMessagingTemplate websocket) {
        this.websocket = websocket;
    }

    @HandleAfterSave
    public void changeIndicator(AppUserIndicator indicator) {
        this.websocket.convertAndSend(
                TOPICS_PREFIX + "/indicator-change", indicator
        );
    }
}
