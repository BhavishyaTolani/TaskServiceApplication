package com.example.TaskApplication.repositories;

import com.example.TaskApplication.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findById(long id);

    Optional<User> findByEmail(String emailId);
}
