package com.xmessenger.model.services.user.validator;

import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.services.user.security.CredentialsService;
import com.xmessenger.model.services.user.security.RawCredentials;
import com.xmessenger.model.services.user.dao.UserDAO;
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
    public void isUsernameUnique() {
        AppUser testUser1 = UserDataFactory.generateSuccessUser();
        Mockito.when(this.userDAO.getUserByUsername(testUser1.getUsername())).thenReturn(null);
        UserValidationResult validationResult1 = this.userValidator.isUsernameUnique(testUser1.getUsername());
        assertTrue(validationResult1.isValid());
        AppUser testUser2 = UserDataFactory.generateFailureUser();
        Mockito.when(this.userDAO.getUserByUsername(testUser2.getUsername())).thenReturn(testUser2);
        UserValidationResult validationResult2 = this.userValidator.isUsernameUnique(testUser2.getUsername());
        assertTrue(!validationResult2.isValid());
    }

    @Test
    public void validateOnRegistration() {
        AppUser testUser = UserDataFactory.generateSuccessUser();
        Mockito.when(this.userDAO.getUserByUsername(testUser.getUsername())).thenReturn(null);
        UserValidationResult validationResult1 = this.userValidator.validateOnRegistration(testUser);
        assertTrue(validationResult1.isValid());
        testUser.setName("Invalid - name #123");
        UserValidationResult validationResult2 = this.userValidator.validateOnRegistration(testUser);
        assertTrue(!validationResult2.isValid());
    }

    @Test
    public void validateOnPasswordChange() {
        AppUser testUser = UserDataFactory.generateSuccessUser();
        RawCredentials rawCredentials = UserDataFactory.composeRawCredentials(testUser);
        rawCredentials.setNewPassword("77");
        // Invalid password;
        UserValidationResult validationResult = this.userValidator.validateOnPasswordChange(testUser, rawCredentials);
        assertFalse(validationResult.isValid());
        // Password not confirmed;
        rawCredentials.setNewPassword("pwd_super_7");
        Mockito.when(this.credentialsService.matchesPassword(rawCredentials.getPassword(), testUser)).thenReturn(false);
        validationResult = this.userValidator.validateOnPasswordChange(testUser, rawCredentials);
        assertFalse(validationResult.isValid());
        // Password cannot be the same;
        Mockito.when(this.credentialsService.matchesPassword(rawCredentials.getPassword(), testUser)).thenReturn(true);
        Mockito.when(this.credentialsService.matchesPassword(rawCredentials.getNewPassword(), testUser)).thenReturn(true);
        validationResult = this.userValidator.validateOnPasswordChange(testUser, rawCredentials);
        assertFalse(validationResult.isValid());
        // Correct;
        Mockito.when(this.credentialsService.matchesPassword(rawCredentials.getPassword(), testUser)).thenReturn(true);
        Mockito.when(this.credentialsService.matchesPassword(rawCredentials.getNewPassword(), testUser)).thenReturn(false);
        validationResult = this.userValidator.validateOnPasswordChange(testUser, rawCredentials);
        assertTrue(validationResult.isValid());
    }

    @Test
    public void validateOnProfileChange() {
        AppUser testUser = UserDataFactory.generateSuccessUser();
        testUser.setId(0);
        // Invalid User ID;
        UserValidationResult validationResult = this.userValidator.validateOnProfileChange(testUser);
        assertFalse(validationResult.isValid());
        // Invalid value in 'Name' field;
        testUser.setId(1234);
        testUser.setName("Invalid - name_7");
        validationResult = this.userValidator.validateOnProfileChange(testUser);
        assertFalse(validationResult.isValid());
        // Correct;
        testUser.setName("Success");
        validationResult = this.userValidator.validateOnProfileChange(testUser);
        assertTrue(validationResult.isValid());
    }
}