package com.ps.user.controller;

import com.ps.user.entity.User;
import com.ps.user.service.UserService;
import graphql.GraphQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @MutationMapping
    public User addUser(@Argument User user) {
        logger.info("Received request to add user: {}", user.getEmail());
        User createdUser = userService.addUser(user);
        logger.info("User created with ID: {}", createdUser.getUserId());
        return createdUser;
    }

    @QueryMapping
    public List<User> getAllUsers() {
        logger.info("Fetching all users");
        List<User> users = userService.getAllUsers();
        logger.info("Total users fetched: {}", users.size());
        return users;
    }

    @QueryMapping
    public User getUserById(@Argument Long id) {
        logger.info("Fetching user with ID: {}", id);
        User user = userService.getUserById(id);
        logger.info("User fetched: {}", user.getEmail());
        return user;
    }

    @MutationMapping
    public User updateUser(@Argument Long id, @Argument User user) {
        logger.info("Updating user with ID: {}", id);
        User updatedUser = userService.updateUser(id, user);
        logger.info("User updated: {}", updatedUser.getEmail());
        return updatedUser;
    }

    @MutationMapping
    public String deleteUser(@Argument Long id) {
        logger.info("Deleting user with ID: {}", id);
        String response = userService.deleteUser(id);
        logger.info("Delete response: {}", response);
        return response;
    }
}
