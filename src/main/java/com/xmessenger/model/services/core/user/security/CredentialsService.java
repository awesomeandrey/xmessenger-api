package com.xmessenger.model.services.core.user.security;

import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.util.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class CredentialsService {
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CredentialsService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public void encodePassword(AppUser user) {
        user.setPassword(
                this.passwordEncoder.encode(user.getPassword())
        );
    }

    public boolean matchesPassword(String rawPassword, AppUser userToVerify) {
        String encodedPassword = userToVerify.getPassword();
        return Utility.isNotBlank(rawPassword)
                && Utility.isNotBlank(encodedPassword)
                && this.passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public String generateRandomPassword(int len) {
        String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        }
        return sb.toString();
    }
}
