package com.xmessenger.model.services.core.user.dao;

import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.database.repositories.core.UserRepository;
import com.xmessenger.model.util.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UserDAO {
    private final UserRepository userRepository;

    @Autowired
    public UserDAO(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<AppUser> search(QueryParams params) {
        if (Utility.isBlank(params.getNameOrLogin())) {
            return new ArrayList<>();
        }
        if (params.isSearchByLogin()) {
            return this.userRepository.findTop5ByUsernameContainingAndActiveTrue(params.getNameOrLogin());
        } else {
            return this.userRepository.findTop5ByNameContainingAndActiveTrue(params.getNameOrLogin());
        }
    }

    public AppUser getUserById(Integer uid) {
        if (Utility.isBlank(uid)) return null;
        return this.userRepository.findOne(uid);
    }

    public AppUser getUserByUsername(String username) {
        if (Utility.isBlank(username)) return null;
        return this.userRepository.findByUsername(username);
    }

    public AppUser create(AppUser userToSave) {
        userToSave.setLastLogin(new Date());
        userToSave.setActive(true);
        return this.userRepository.save(userToSave);
    }

    public AppUser update(AppUser userToUpdate) throws UserNotFoundException {
        Integer uid = userToUpdate.getId();
        if (this.userRepository.findOne(uid) == null) {
            throw new UserNotFoundException(uid);
        } else {
            return this.userRepository.save(userToUpdate);
        }
    }

    public void deleteUser(AppUser userToDelete) {
        this.userRepository.delete(userToDelete);
    }

    public static class UserNotFoundException extends IllegalArgumentException {
        public UserNotFoundException() {
            this(0);
        }

        public UserNotFoundException(Integer uid) {
            super(String.format("User with ID=%d was not found.", uid));
        }

        public UserNotFoundException(AppUser appUser) {
            this(appUser.getId());
        }
    }
}
