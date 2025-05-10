package com.ps.user.service;

import com.ps.user.entity.Address;
import com.ps.user.entity.User;
import com.ps.user.exception.DuplicateUserException;
import com.ps.user.exception.UserNotFoundException;
import com.ps.user.repository.AddressRepository;
import com.ps.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional
    public User addUser(User user) {
        logger.info("Attempting to add user with email: {}", user.getEmail());
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());

        if (existingUser.isPresent()) {
            logger.warn("Duplicate user found with email: {}", user.getEmail());
            throw new DuplicateUserException("User with email: %s already exists.", user.getEmail());
        }

        Address address = user.getAddress();
        if (address != null) {
            logger.debug("Verifying address for user: {}", user.getEmail());
            Address verifiedAddress = verifyAddress(address);
            user.setAddress(verifiedAddress);
        }
        User savedUser = userRepository.save(user);
        logger.info("User saved successfully with ID: {}", savedUser.getUserId());
        return savedUser;
    }

    private Address verifyAddress(Address address) {
        logger.debug("Checking if address exists: {}, {}, {}, {}, {}",
                address.getStreet(), address.getCity(), address.getState(), address.getPinCode(), address.getCountry());

        Optional<Address> existingAddress = addressRepository.findByStreetAndCityAndStateAndPinCodeAndCountry(
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getPinCode(),
                address.getCountry()
        );
        if (existingAddress.isPresent()) {
            logger.debug("Existing address found, reusing address ID: {}", existingAddress.get().getAddressId());
            return existingAddress.get();
        } else {
            Address savedAddress = addressRepository.save(address);
            logger.debug("New address saved with ID: {}", savedAddress.getAddressId());
            return savedAddress;
        }
    }

    @Override
    public List<User> getAllUsers() {
        logger.info("Fetching all users");
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        logger.info("Fetching user with ID: {}", id);
        String key = "user:" + id;

        User user = (User) redisTemplate.opsForValue().get(key);
        if (user != null) {
            logger.info("User fetched from Redis Cache");
            return user;
        }

        user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User not found with ID: {}", id);
                    return new UserNotFoundException("User with id : %d not found", id);
                });
        redisTemplate.opsForValue().set(key, user);
        return user;
    }

    @Override
    public User updateUser(Long id, User user) {
        logger.info("Attempting to update user with ID: {}", id);
        String key = "user:" + id;

        User cachedUser = (User) redisTemplate.opsForValue().get(key);
        Optional<User> userDetails;
        if (cachedUser != null) {
            logger.info("User fetched from cache");
            userDetails = Optional.of(cachedUser);
        } else {
            userDetails = userRepository.findById(id);
        }

        if (userDetails.isPresent()) {
            User existingUser = userDetails.get();

            if (user.getPhoneNo() != null && !user.getPhoneNo().isEmpty()) {
                existingUser.setPhoneNo(user.getPhoneNo());
                logger.debug("Updated phone number for user ID: {}", id);
            }

            if (user.getAddress() != null) {
                logger.debug("Updating address for user ID: {}", id);
                Address verifiedAddress = verifyAddress(user.getAddress());
                existingUser.setAddress(verifiedAddress);
            }
            User updatedUser = userRepository.save(existingUser);
            logger.info("User updated successfully with ID: {}", id);
            redisTemplate.opsForValue().set(key, updatedUser);
            return updatedUser;
        }
        logger.warn("User not found for updating with ID: {}", id);
        throw new UserNotFoundException("User with id : %d not found", id);
    }

    @Override
    public String deleteUser(Long id) {
        logger.info("Attempting to delete user with ID: {}", id);

        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            logger.info("User deleted successfully with ID: {}", id);
            return String.format("User with id: %d deleted successfully", id);
        }

        logger.warn("User not found for deletion with ID: {}", id);
        throw new UserNotFoundException("User with id : %d not found", id);
    }
}
