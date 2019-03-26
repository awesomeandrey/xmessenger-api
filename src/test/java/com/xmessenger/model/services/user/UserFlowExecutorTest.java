package com.xmessenger.model.services.user;

import com.xmessenger.model.database.entities.ApplicationUser;
import com.xmessenger.model.services.user.security.CredentialsService;
import com.xmessenger.model.services.user.security.RawCredentials;
import com.xmessenger.model.services.user.dao.UserDAO;
import com.xmessenger.model.services.user.validator.UserValidationResult;
import com.xmessenger.model.services.user.validator.UserValidator;
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
@SpringBootTest(classes = UserFlowExecutor.class)
public class UserFlowExecutorTest {
    @MockBean
    private UserDAO userDAO;
    @MockBean
    private UserValidator userValidator;
    @MockBean
    private CredentialsService credentialsService;

    @Autowired
    private UserFlowExecutor userFlowExecutor;

    @Test
    public void lookup() throws Exception {
        ApplicationUser testUser = UserDataFactory.generateSuccessUser();
        RawCredentials rawCredentials = UserDataFactory.composeRawCredentials(testUser);
        Mockito.when(this.userDAO.getUserByUsername(testUser.getUsername())).thenReturn(testUser);
        Mockito.when(this.credentialsService.matchesPassword(rawCredentials.getPassword(), testUser)).thenReturn(true);
        ApplicationUser foundUser = this.userFlowExecutor.lookupUser(rawCredentials);
        assertEquals(testUser.getId(), foundUser.getId());
    }

    @Test
    public void lookup_NotFound() {
        ApplicationUser testUser = UserDataFactory.generateSuccessUser();
        Mockito.when(this.userDAO.getUserByUsername(testUser.getUsername())).thenReturn(null);
        ApplicationUser foundUser = null;
        try {
            RawCredentials rawCredentials = UserDataFactory.composeRawCredentials(testUser);
            foundUser = this.userFlowExecutor.lookupUser(rawCredentials);
        } catch (Exception e) {
            assertNull(foundUser);
        }
    }

    @Test
    public void register() throws Exception {
        ApplicationUser testUser = UserDataFactory.generateSuccessUser();
        UserValidationResult validationResult = new UserValidationResult(true);
        Mockito.when(this.userValidator.validateOnRegistration(testUser)).thenReturn(validationResult);
        Mockito.when(this.userDAO.create(testUser)).thenReturn(testUser);
        ApplicationUser registeredUser = this.userFlowExecutor.registerUser(testUser);
        assertNotNull(registeredUser);
        assertEquals(testUser.getUsername(), registeredUser.getUsername());
    }

    @Test
    public void register_InvalidData() {
        ApplicationUser testUser = UserDataFactory.generateSuccessUser();
        UserValidationResult validationResult = new UserValidationResult(false);
        Mockito.when(this.userValidator.validateOnRegistration(testUser)).thenReturn(validationResult);
        ApplicationUser registeredUser = null;
        try {
            registeredUser = this.userFlowExecutor.registerUser(testUser);
        } catch (Exception e) {
            assertNull(registeredUser);
        }
    }

    @Test
    public void changeProfileInfo() throws Exception {
        ApplicationUser testUser = UserDataFactory.generateSuccessUser();
        String oldName = testUser.getName(), newName = "NEW NAME";
        testUser.setName(newName);
        Mockito.when(this.userValidator.validateOnProfileChange(testUser)).thenReturn(new UserValidationResult(true));
        Mockito.when(this.userDAO.getUserById(testUser.getId())).thenReturn(testUser);
        Mockito.when(this.userDAO.update(testUser)).thenReturn(testUser);
        ApplicationUser updatedUser = this.userFlowExecutor.changeProfileInfo(testUser);
        assertEquals(testUser.getId(), updatedUser.getId());
        assertNotEquals(oldName, testUser.getName());
    }

    @Test
    public void changeProfileInfo_InvalidData() {
        ApplicationUser testUser = UserDataFactory.generateSuccessUser();
        testUser.setName(testUser.getName().concat("_CHANGED"));
        Mockito.when(this.userValidator.validateOnProfileChange(testUser)).thenReturn(new UserValidationResult(false));
        ApplicationUser updatedUser = null;
        try {
            updatedUser = this.userFlowExecutor.changeProfileInfo(testUser);
        } catch (Exception ignored) {
            assertNull(updatedUser);
        }
    }

    @Test
    public void changePassword() throws Exception {
        ApplicationUser testUser = UserDataFactory.generateSuccessUser();
        RawCredentials rawCredentials = UserDataFactory.composeRawCredentials(testUser);
        rawCredentials.setNewPassword("new_password_1234");
        Mockito.when(this.userValidator.validateOnPasswordChange(testUser, rawCredentials)).thenReturn(new UserValidationResult(true));
        Mockito.when(this.userDAO.getUserById(testUser.getId())).thenReturn(testUser);
        Mockito.when(this.userDAO.update(testUser)).thenReturn(testUser);
        ApplicationUser foundUser = this.userFlowExecutor.changePassword(testUser, rawCredentials);
        assertEquals(testUser.getId(), foundUser.getId());
        assertNotEquals(rawCredentials.getPassword(), foundUser.getPassword());
    }

    @Test
    public void changePassword_InvalidData() {
        ApplicationUser testUser = UserDataFactory.generateSuccessUser();
        RawCredentials rawCredentials = UserDataFactory.composeRawCredentials(testUser);
        rawCredentials.setNewPassword("new_password_1234");
        Mockito.when(this.userValidator.validateOnPasswordChange(testUser, rawCredentials)).thenReturn(new UserValidationResult(false));
        ApplicationUser updatedUser = null;
        try {
            updatedUser = this.userFlowExecutor.changePassword(testUser, rawCredentials);
        } catch (Exception e) {
            assertNull(updatedUser);
        }
    }
}