package com.example.demo.Repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Model.User;

public interface UserRepo extends JpaRepository<User, Long> {

	boolean existsByEmail(String email);

	User findByEmailAndPassword(String email, String password);

	Optional<User> findByEmail(String email);

}
