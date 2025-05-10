package com.ps.user.repository;

import com.ps.user.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {

    Optional<Address> findByStreetAndCityAndStateAndPinCodeAndCountry(
            String street,
            String city,
            String state,
            String pinCode,
            String country);
}
