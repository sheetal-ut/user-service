package com.ps.user.steps;

import com.ps.user.entity.User;
import com.ps.user.entity.Address;
import com.ps.user.exception.DuplicateUserException;
import com.ps.user.exception.UserNotFoundException;
import com.ps.user.repository.AddressRepository;
import com.ps.user.repository.UserRepository;
import com.ps.user.service.UserService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test") // Ensure we are using a test profile
//@ContextConfiguration(initializers = TestPostgresConfig.class)
public class UserServiceDefinitions {

    @Autowired
    private UserService userService;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Exception exception;
    private DuplicateUserException duplicateUserException;
    private UserNotFoundException userNotFoundException;

    @AfterEach
    public void after_each(){
        userRepository.deleteAll();
        addressRepository.deleteAll();
    }

    @Then("print datasource url")
    public void print_datasource_url() {
        System.out.println("Datasource URL: " +
                org.springframework.test.util.ReflectionTestUtils.getField(
                        userRepository, "dataSource"));
    }

    @Given("I have a valid user")
    public void i_have_a_valid_user() {
        String email = "test" + UUID.randomUUID() + "@example.com";
        Address address = new Address();
        address.setStreet("123 Main");
        address.setCity("CityX");
        address.setState("StateX");
        address.setPinCode("12345");
        address.setCountry("CountryX");

        testUser = new User();
        testUser.setEmail(email);
        testUser.setPhoneNo("1234567890");
        testUser.setAddress(address);
    }

    @When("I add the user to the system")
    public void i_add_the_user_to_the_system() {
        try {
            userService.addUser(testUser);
        } catch (Exception e) {
            exception = e;
        }
    }

    @Then("the user should be added successfully")
    public void the_user_should_be_added_successfully() {
        assertNull(exception);
    }

    @Given("I have an user with an email already in the system")
    public void i_have_an_user_with_email_already_in_the_system() {
        String email = "test" + UUID.randomUUID() + "@example.com";
        testUser = new User();
        testUser.setEmail(email);
        testUser.setPhoneNo("1234567890");

        // Add the user to the system first to simulate the pre-existing state
        try {
            userService.addUser(testUser);
        } catch (DuplicateUserException e) {
            duplicateUserException = e;
        }

    }

    @When("I try to add the user again")
    public void i_try_to_add_the_user_again() {
        try {
            userService.addUser(testUser);
        } catch (DuplicateUserException e) {
            duplicateUserException = e;
        }
    }

    @Then("I should receive a {string}")
    public void i_should_receive_a(String expectedException) {
        assertNotNull(duplicateUserException);
        assertEquals(expectedException, duplicateUserException.getClass().getSimpleName());
    }

    @Given("an address {string} already exists")
    public void an_address_already_exists(String addressStr) {
        userRepository.deleteAll();
        addressRepository.deleteAll();

        String[] parts = addressStr.split(",\\s*");
        Address address = new Address();
        address.setStreet(parts[0]);
        address.setCity(parts[1]);
        address.setState(parts[2]);
        address.setPinCode(parts[3]);
        address.setCountry(parts[4]);

        addressRepository.save(address);
    }

    @Given("I have a new user with same address and a unique email")
    public void i_have_a_new_user_with_same_address_and_a_unique_email() {
        String uniqueEmail = "test" + UUID.randomUUID() + "@example.com";

        Address address = addressRepository.findAll().get(0); // reuse existing

        testUser = new User();
        testUser.setEmail(uniqueEmail);
        testUser.setPhoneNo("1234567890");
        testUser.setAddress(address);
    }

    @Then("the address should be reused and not duplicated")
    public void the_address_should_be_reused_and_not_duplicated() {
        Address originalAddress = testUser.getAddress();
        Optional<Address> addressOptional = addressRepository.findByStreetAndCityAndStateAndPinCodeAndCountry(
                originalAddress.getStreet(),
                originalAddress.getCity(),
                originalAddress.getState(),
                originalAddress.getPinCode(),
                originalAddress.getCountry()
        );
        int count = addressOptional.isPresent() ? 1 : 0;

        assertEquals(1, count, "Address was duplicated instead of reused.");
    }

    @Given("the system contains 2 users")
    public void the_system_contains_users_with_emails() {
        userRepository.deleteAll();
        addressRepository.deleteAll();

        String email1 = "test" + UUID.randomUUID() + "@example.com";
        String email2 = "test" + UUID.randomUUID() + "@example.com";

        Address address1 = new Address();
        address1.setStreet("street1");
        address1.setCity("city1");
        address1.setState("state1");
        address1.setPinCode("11AA11");
        address1.setCountry("country1");

        User user1 = new User();
        user1.setEmail(email1);
        user1.setPhoneNo("1111111111");
        user1.setAddress(address1);

        Address address2 = new Address();
        address2.setStreet("street2");
        address2.setCity("city2");
        address2.setState("state2");
        address2.setPinCode("11AA22");
        address2.setCountry("country2");

        User user2 = new User();
        user2.setEmail(email2);
        user2.setPhoneNo("2222222222");
        user2.setAddress(address2);

        userService.addUser(user1);
        userService.addUser(user2);
    }

    List<User> retrievedUsers;

    @When("I retrieve all users")
    public void i_retrieve_all_users() {
        retrievedUsers = userService.getAllUsers();
    }

    @Then("I should get a list containing {int} users")
    public void i_should_get_a_list_containing_users(Integer expectedCount) {
        assertNotNull(retrievedUsers);
        assertEquals(expectedCount, retrievedUsers.size(), "User count mismatch");
    }
}
