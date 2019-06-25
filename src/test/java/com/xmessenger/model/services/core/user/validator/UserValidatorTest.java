package com.xmessenger.model.services.core.user.validator;

import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.services.core.user.security.CredentialsService;
import com.xmessenger.model.services.core.user.security.decorators.RawCredentials;
import com.xmessenger.model.services.core.user.dao.UserDAO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import data.factories.UserDataFactory;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserValidator.class)
public class UserValidatorTest {
    @MockBean
    private UserDAO userDAO;
    @MockBean
    private CredentialsService credentialsService;
    @Autowired
    private UserValidator userValidator;

    @Test
    public void validateOnPasswordChange() {
        AppUser testUser = UserDataFactory.generateSuccessUser();
        RawCredentials rawCredentials = UserDataFactory.composeRawCredentials(testUser);
        rawCredentials.setNewPassword("77");
        // Invalid password;
        UserValidator.Result validationResult = this.userValidator.validateOnPasswordChange(testUser, rawCredentials);
        assertFalse(validationResult.isValid());
        // Password not confirmed;
        rawCredentials.setNewPassword("pwd_super_7");
        Mockito.when(this.credentialsService.matchesPassword(rawCredentials.getPassword(), testUser.getPassword())).thenReturn(false);
        validationResult = this.userValidator.validateOnPasswordChange(testUser, rawCredentials);
        assertFalse(validationResult.isValid());
        // Password cannot be the same;
        Mockito.when(this.credentialsService.matchesPassword(rawCredentials.getPassword(), testUser.getPassword())).thenReturn(true);
        Mockito.when(this.credentialsService.matchesPassword(rawCredentials.getNewPassword(), testUser.getPassword())).thenReturn(true);
        validationResult = this.userValidator.validateOnPasswordChange(testUser, rawCredentials);
        assertFalse(validationResult.isValid());
        // Correct;
        Mockito.when(this.credentialsService.matchesPassword(rawCredentials.getPassword(), testUser.getPassword())).thenReturn(true);
        Mockito.when(this.credentialsService.matchesPassword(rawCredentials.getNewPassword(), testUser.getPassword())).thenReturn(false);
        validationResult = this.userValidator.validateOnPasswordChange(testUser, rawCredentials);
        assertTrue(validationResult.isValid());
    }
}