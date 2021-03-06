package com.xmessenger.model.services.async;

import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.services.core.user.indicators.IndicatorService;
import com.xmessenger.model.services.core.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AsynchronousService {
    @Qualifier("asyncService")
    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private UserService userService;

    @Autowired
    private IndicatorService indicatorService;

    public void renewLastLogin(String username) {
        this.taskExecutor.execute(() -> {
            AppUser appUser = this.userService.lookupUser(username);
            this.renewLastLogin(appUser);
        });
    }

    public void renewLastLogin(AppUser appUser) {
        this.taskExecutor.execute(() -> {
            try {
                appUser.setLastLogin(new Date());
                this.userService.changeProfileInfo(appUser);
            } catch (Exception e) {
                System.err.println(">>> Could not set 'last_login'. " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public void switchAppUserIndicator(String username, boolean active) {
        this.taskExecutor.execute(() -> {
            AppUser appUser = userService.lookupUser(username);
            if (appUser != null) switchAppUserIndicator(appUser, active);
        });
    }

    public void switchAppUserIndicator(AppUser appUser, boolean active) {
        this.taskExecutor.execute(() -> {
            indicatorService.switchIndicator(appUser, active);
        });
    }
}