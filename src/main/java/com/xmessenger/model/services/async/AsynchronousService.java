package com.xmessenger.model.services.async;

import com.xmessenger.model.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class AsynchronousService {
    @Autowired
    private ApplicationContext applicationContext;

    @Qualifier("asyncService")
    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private UserService userService;

    public void renewLastLoginByUsername(String username) {
        this.taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                userService.renewLastLogin(username);
            }
        });
    }
}