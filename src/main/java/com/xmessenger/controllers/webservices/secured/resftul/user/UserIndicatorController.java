package com.xmessenger.controllers.webservices.secured.resftul.user;

import com.xmessenger.controllers.webservices.open.websockets.config.WebSocketConfig;
import com.xmessenger.model.database.entities.core.AppUser;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Date;

@Controller
public class UserIndicatorController {
    private static final String API_PATH = "/indicator-change";

    @MessageMapping(API_PATH)
    @SendTo(WebSocketConfig.TOPICS_PREFIX + API_PATH)
    public Indicator switchStatus(Indicator indicator) {
        System.out.println("Switching indicator: " + indicator.toString());
        return indicator;
    }

    /**
     * This inner class cannot be a non-static inner class.
     * It boils down to the way Java added inner classes means they don't have the default constructor that Jackson requires.
     */
    public static class Indicator {
        private AppUser user;
        private boolean loggedIn;
        private Date dateStamp;

        public AppUser getUser() {
            return user;
        }

        public void setUser(AppUser user) {
            this.user = user;
        }

        public boolean isLoggedIn() {
            return loggedIn;
        }

        public void setLoggedIn(boolean loggedIn) {
            this.loggedIn = loggedIn;
        }

        public Date getDateStamp() {
            return dateStamp;
        }

        public void setDateStamp(Date dateStamp) {
            this.dateStamp = dateStamp;
        }

        public Indicator() {
            this.loggedIn = false;
        }

        @Override
        public String toString() {
            return "Indicator{" +
                    "user=[" + user.getId() + ":" + user.getName() +
                    "], loggedIn=" + loggedIn +
                    ", dateStamp=" + dateStamp +
                    '}';
        }
    }
}
