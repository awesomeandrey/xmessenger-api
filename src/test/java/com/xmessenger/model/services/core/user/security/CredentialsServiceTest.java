package com.xmessenger.model.services.core.user.security;

import com.xmessenger.model.database.entities.core.AppUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import data.factories.UserDataFactory;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CredentialsService.class)
public class CredentialsServiceTest {
    @MockBean
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CredentialsService credentialsService;

    @Test
    public void encodePassword() {
        String rawPassword = this.credentialsService.generateRandomPassword(8);
        assertNotEquals(rawPassword, this.credentialsService.encodePassword(rawPassword));
    }

    @Test
    public void matchesPassword() {
        AppUser testUser = UserDataFactory.generateSuccessUser();
        String rawPassword = "raw_password";
        Mockito.when(this.passwordEncoder.matches(rawPassword, testUser.getPassword())).thenReturn(true);
        assertTrue(this.credentialsService.matchesPassword(rawPassword, testUser.getPassword()));
    }
}