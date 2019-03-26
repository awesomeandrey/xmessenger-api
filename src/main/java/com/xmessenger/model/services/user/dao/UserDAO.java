package com.xmessenger.model.services.user.dao;

import com.xmessenger.model.database.entities.ApplicationUser;
import com.xmessenger.model.database.repositories.UserRepository;
import com.xmessenger.model.util.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDAO {
    private final UserRepository userRepository;

    @Autowired
    public UserDAO(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<ApplicationUser> search(QueryParams params) {
        if (Utility.isBlank(params.getNameOrLogin())) {
            return new ArrayList<>();
        }
        if (params.isSearchByLogin()) {
            return this.userRepository.findTop5ByUsernameContainingAndActiveTrue(params.getNameOrLogin());
        } else {
            return this.userRepository.findTop5ByNameContainingAndActiveTrue(params.getNameOrLogin());
        }
    }

    public ApplicationUser getUserById(Integer uid) {
        if (Utility.isBlank(uid)) return null;
        return this.userRepository.findOne(uid);
    }

    public ApplicationUser getUserByUsername(String username) {
        if (Utility.isBlank(username)) return null;
        return this.userRepository.findByUsername(username);
    }

    public ApplicationUser create(ApplicationUser userToSave) {
        return this.userRepository.save(userToSave);
    }

    public ApplicationUser update(ApplicationUser userToUpdate) throws UserNotFoundException {
        Integer uid = userToUpdate.getId();
        if (this.userRepository.findOne(uid) == null) {
            throw new UserNotFoundException(uid);
        } else {
            return this.userRepository.save(userToUpdate);
        }
    }

    public byte[] getPicture(Integer uid) throws UserNotFoundException {
        ApplicationUser user = this.userRepository.findOne(uid);
        if (user == null) {
            throw new UserNotFoundException(uid);
        }
        return user.getPicture();
    }

    public class UserNotFoundException extends Exception {
        public UserNotFoundException(Integer i) {
            super("Could not find user with ID=[" + i + "].");
        }
    }
}
