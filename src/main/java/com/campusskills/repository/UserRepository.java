package com.campusskills.repository;

import com.campusskills.entity.Role;
import com.campusskills.entity.User;
import com.campusskills.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByRollNumber(String rollNumber);
    Optional<User> findByEmailOrRollNumber(String email, String rollNumber);
    boolean existsByEmail(String email);
    boolean existsByRollNumber(String rollNumber);
    List<User> findByStatus(UserStatus status);
    List<User> findByRole(Role role);
    long countByRole(Role role);
    long countByStatus(UserStatus status);
}
