package com.xmessenger.model.services.core.user;

import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.services.core.chatter.RelationService;
import com.xmessenger.model.services.core.user.security.CredentialsService;
import com.xmessenger.model.services.core.user.security.RawCredentials;
import com.xmessenger.model.services.core.user.dao.UserDAO;
import com.xmessenger.model.services.core.user.validator.UserValidationResult;
import com.xmessenger.model.services.core.user.validator.UserValidator;
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
@SpringBootTest(classes = UserService.class)
public class UserServiceTest {
    @MockBean
    private UserDAO userDAO;
    @MockBean
    private UserValidator userValidator;
    @MockBean
    private CredentialsService credentialsService;
    @MockBean
    private RelationService relationService;

    @Autowired
    private UserService userService;

    @Test
    public void lookup() throws Exception {
        AppUser testUser = UserDataFactory.generateSuccessUser();
        RawCredentials rawCredentials = UserDataFactory.composeRawCredentials(testUser);
        Mockito.when(this.userDAO.getUserByUsername(testUser.getUsername())).thenReturn(testUser);
        Mockito.when(this.credentialsService.matchesPassword(rawCredentials.getPassword(), testUser)).thenReturn(true);
        AppUser foundUser = this.userService.lookupUser(rawCredentials);
        assertEquals(testUser.getId(), foundUser.getId());
    }

    @Test
    public void lookup_NotFound() {
        AppUser testUser = UserDataFactory.generateSuccessUser();
        Mockito.when(this.userDAO.getUserByUsername(testUser.getUsername())).thenReturn(null);
        AppUser foundUser = null;
        try {
            RawCredentials rawCredentials = UserDataFactory.composeRawCredentials(testUser);
            foundUser = this.userService.lookupUser(rawCredentials);
        } catch (Exception e) {
            assertNull(foundUser);
        }
    }

    @Test
    public void register() throws Exception {
        AppUser testUser = UserDataFactory.generateSuccessUser();
        Mockito.when(this.userDAO.create(testUser)).thenReturn(testUser);
        AppUser registeredUser = this.userService.registerUser(testUser);
        assertNotNull(registeredUser);
        assertEquals(testUser.getUsername(), registeredUser.getUsername());
    }

    @Test
    public void register_InvalidData() {
        AppUser testUser = UserDataFactory.generateSuccessUser();
        AppUser registeredUser = null;
        try {
            registeredUser = this.userService.registerUser(testUser);
        } catch (Exception e) {
            assertNull(registeredUser);
        }
    }

    @Test
    public void changeProfileInfo() throws Exception {
        AppUser testUser = UserDataFactory.generateSuccessUser();
        String oldName = testUser.getName(), newName = "NEW NAME";
        testUser.setName(newName);
        Mockito.when(this.userDAO.getUserById(testUser.getId())).thenReturn(testUser);
        Mockito.when(this.userDAO.update(testUser)).thenReturn(testUser);
        AppUser updatedUser = this.userService.changeProfileInfo(testUser);
        assertEquals(testUser.getId(), updatedUser.getId());
        assertNotEquals(oldName, testUser.getName());
    }

    @Test
    public void changeProfileInfo_InvalidData() {
        AppUser testUser = UserDataFactory.generateSuccessUser();
        testUser.setName(testUser.getName().concat("_CHANGED"));
        AppUser updatedUser = null;
        try {
            updatedUser = this.userService.changeProfileInfo(testUser);
        } catch (Exception ignored) {
            assertNull(updatedUser);
        }
    }

    @Test
    public void changePassword() throws Exception {
        AppUser testUser = UserDataFactory.generateSuccessUser();
        RawCredentials rawCredentials = UserDataFactory.composeRawCredentials(testUser);
        rawCredentials.setNewPassword("new_password_1234");
        Mockito.when(this.userValidator.validateOnPasswordChange(testUser, rawCredentials)).thenReturn(new UserValidationResult(true));
        Mockito.when(this.userDAO.getUserById(testUser.getId())).thenReturn(testUser);
        Mockito.when(this.userDAO.update(testUser)).thenReturn(testUser);
        AppUser foundUser = this.userService.changePassword(testUser, rawCredentials);
        assertEquals(testUser.getId(), foundUser.getId());
        assertNotEquals(rawCredentials.getPassword(), foundUser.getPassword());
    }

    @Test
    public void changePassword_InvalidData() {
        AppUser testUser = UserDataFactory.generateSuccessUser();
        RawCredentials rawCredentials = UserDataFactory.composeRawCredentials(testUser);
        rawCredentials.setNewPassword("new_password_1234");
        Mockito.when(this.userValidator.validateOnPasswordChange(testUser, rawCredentials)).thenReturn(new UserValidationResult(false));
        AppUser updatedUser = null;
        try {
            updatedUser = this.userService.changePassword(testUser, rawCredentials);
        } catch (Exception e) {
            assertNull(updatedUser);
        }
    }
}