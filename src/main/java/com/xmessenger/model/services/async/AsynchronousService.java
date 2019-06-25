package com.xmessenger.model.services.async;

import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.services.core.user.exceptions.UserNotFoundException;
import com.xmessenger.model.services.core.user.indicators.IndicatorService;
import com.xmessenger.model.services.core.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AsynchronousService {
    @Autowired
    private ApplicationContext applicationContext;

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
            renewLastLogin(appUser);
        });
    }

    public void renewLastLogin(AppUser appUser) {
        this.taskExecutor.execute(() -> {
            try {
                appUser.setLastLogin(new Date());
                this.userService.changeProfileInfo(appUser);
            } catch (Exception e) {
                System.err.println(">>> Could not set 'last_login'. " + e.getMessage());
            }
        });
    }

    public void switchAppUserIndicator(String username, boolean active) {
        this.taskExecutor.execute(() -> {
            AppUser appUser = userService.lookupUser(username);
            indicatorService.switchIndicator(appUser, active);
        });
    }
}