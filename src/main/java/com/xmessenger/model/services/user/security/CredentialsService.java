package com.xmessenger.model.services.user.security;

import com.xmessenger.model.database.entities.ApplicationUser;
import com.xmessenger.model.util.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CredentialsService {
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CredentialsService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public void encodePassword(ApplicationUser user) {
        user.setPassword(
                this.passwordEncoder.encode(user.getPassword())
        );
    }

    public boolean matchesPassword(String rawPassword, ApplicationUser userToVerify) {
        String encodedPassword = userToVerify.getPassword();
        return Utility.isNotBlank(rawPassword)
                && Utility.isNotBlank(encodedPassword)
                && this.passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
