package com.spribe.services.units.booking.service.model.repo;

import com.spribe.services.units.booking.service.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> getByEmail(String email);

    Page<User> getAllByName(Pageable pageable, String name);

    Boolean existsByEmail(String email);
}