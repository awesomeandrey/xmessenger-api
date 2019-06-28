package com.xmessenger.model.services.core.user;

import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.services.core.user.security.CredentialsService;
import com.xmessenger.model.services.core.user.dao.UserDAO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import data.factories.UserDataFactory;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserService.class)
public class UserServiceTest {
    @MockBean
    private UserDAO userDAO;
    @MockBean
    private CredentialsService credentialsService;

    @Autowired
    private UserService userService;

    @Test
    public void lookup() {
        AppUser testUser = UserDataFactory.generateSuccessUser();
        // Lookup by ID;
        assertNotNull(testUser.getId());
        Mockito.when(this.userDAO.getUserById(testUser.getId())).thenReturn(testUser);
        assertEquals(testUser, this.userService.lookupUser(testUser));
        // Lookup by username;
        assertNotNull(testUser.getUsername());
        Mockito.when(this.userDAO.getUserById(testUser.getId())).thenReturn(null);
        Mockito.when(this.userDAO.getUserByUsername(testUser.getUsername())).thenReturn(testUser);
        assertEquals(testUser, this.userService.lookupUser(testUser));
        // Not found;
        testUser = UserDataFactory.generateFailureUser();
        assertNull(this.userService.lookupUser(testUser));
    }

    @Test
    public void search() {
        List<AppUser> testUsers = UserDataFactory.generateTestUsers();
        assertFalse(testUsers.isEmpty());
        Mockito.when(this.userDAO.search(null)).thenReturn(testUsers);
        assertEquals(testUsers.size(), this.userService.search(null).size());
    }

    @Test
    public void register() {
        AppUser testUser = UserDataFactory.generateSuccessUser();
        Mockito.when(this.userDAO.create(testUser)).thenReturn(testUser);
        AppUser registeredUser = this.userService.registerUser(testUser);
        assertNotNull(registeredUser);
        assertEquals(testUser.getUsername(), registeredUser.getUsername());
    }

    @Test
    public void changeProfileInfo() {
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
    public void changePassword() {
        AppUser testUser = UserDataFactory.generateSuccessUser();
        String newRawPassword = "simple_pwd", encodedPassword = "encoded_password_123";
        Mockito.when(this.credentialsService.encodePassword(newRawPassword)).thenReturn(encodedPassword);
        Mockito.when(this.userDAO.update(testUser)).thenReturn(testUser);
        AppUser updatedUser = this.userService.changePassword(testUser, newRawPassword);
        assertEquals(encodedPassword, updatedUser.getPassword());
    }

    @Test
    public void delete() {
        AppUser testUser = UserDataFactory.generateSuccessUser();
        this.userService.deleteUser(testUser);
        assertNotNull(testUser);
    }
}