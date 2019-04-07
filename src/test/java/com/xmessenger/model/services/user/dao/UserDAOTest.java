package com.xmessenger.model.services.user.dao;

import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.database.repositories.UserRepository;
import data.factories.UserDataFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserDAO.class)
public class UserDAOTest {
    @MockBean
    private UserRepository userRepository;
    @Autowired
    private UserDAO userDAO;

    @Test
    public void search() {
        List<AppUser> testUsers = UserDataFactory.generateTestUsers();
        AppUser user1 = testUsers.get(UserDataFactory.SUCCESS_USER_INDEX);
        // Incorrect parameters;
        QueryParams params = new QueryParams("", true);
        assertTrue(this.userDAO.search(params).isEmpty());
        // Search by login;
        params.setNameOrLogin(user1.getUsername());
        Mockito.when(this.userRepository.findTop5ByUsernameContainingAndActiveTrue(user1.getUsername())).thenReturn(testUsers);
        assertFalse(this.userDAO.search(params).isEmpty());
        // Search by Name;
        params.setNameOrLogin(user1.getName());
        params.setSearchByLogin(false);
        Mockito.when(this.userRepository.findTop5ByNameContainingAndActiveTrue(user1.getName())).thenReturn(testUsers);
        assertFalse(this.userDAO.search(params).isEmpty());
    }

    @Test
    public void getUserById() {
        AppUser testUser = UserDataFactory.generateSuccessUser();
        Mockito.when(this.userRepository.findOne(testUser.getId())).thenReturn(testUser);
        AppUser foundUser = this.userDAO.getUserById(testUser.getId());
        assertEquals(testUser.getId(), foundUser.getId());
    }

    @Test
    public void getUserByUsername() {
        AppUser testUser = UserDataFactory.generateSuccessUser();
        AppUser foundUser = this.userDAO.getUserByUsername("");
        assertNull(foundUser);
        Mockito.when(this.userRepository.findByUsername(testUser.getUsername())).thenReturn(testUser);
        foundUser = this.userDAO.getUserByUsername(testUser.getUsername());
        assertEquals(testUser.getId(), foundUser.getId());
    }

    @Test
    public void create() {
        AppUser testUser = UserDataFactory.generateSuccessUser();
        Mockito.when(this.userRepository.save(testUser)).thenReturn(testUser);
        assertEquals(testUser, this.userDAO.create(testUser));
    }

    @Test
    public void update() {
        AppUser updatedUser = null, testUser = UserDataFactory.generateSuccessUser();
        Mockito.when(this.userRepository.findOne(testUser.getId())).thenReturn(null);
        try {
            updatedUser = this.userDAO.update(testUser);
        } catch (Exception e) {
            assertNull(updatedUser);
        }
        Mockito.when(this.userRepository.findOne(testUser.getId())).thenReturn(testUser);
        Mockito.when(this.userRepository.save(testUser)).thenReturn(testUser);
        try {
            updatedUser = this.userDAO.update(testUser);
        } catch (Exception ignore) {
        }
        assertEquals(testUser.getId(), updatedUser.getId());
    }

    @Test
    public void getPicture() {
        Mockito.when(this.userRepository.findOne(null)).thenReturn(null);
        byte[] picture = null;
        try {
            picture = this.userDAO.getPicture(null);
        } catch (Exception e) {
            assertNull(picture);
        }
        AppUser testUser = UserDataFactory.generateSuccessUser();
        Mockito.when(this.userRepository.findOne(testUser.getId())).thenReturn(testUser);
        try {
            picture = this.userDAO.getPicture(testUser.getId());
        } catch (Exception ignore) {
        }
        assertEquals(testUser.getPicture(), picture);
    }
}