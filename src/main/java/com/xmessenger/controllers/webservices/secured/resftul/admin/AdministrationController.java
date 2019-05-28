package com.xmessenger.controllers.webservices.secured.resftul.admin;

import com.xmessenger.configs.WebSecurityConfig;
import com.xmessenger.model.database.entities.AppUserIndicator;
import com.xmessenger.model.services.IndicatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping(WebSecurityConfig.API_BASE_PATH + "/admin")
public class AdministrationController {
    private final IndicatorService indicatorService;

    @Autowired
    public AdministrationController(IndicatorService indicatorService) {
        this.indicatorService = indicatorService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/indicators", method = RequestMethod.GET)
    public Set<AppUserIndicator> retrieveUsersOnline() {
        return this.indicatorService.getIndicators();
    }
}