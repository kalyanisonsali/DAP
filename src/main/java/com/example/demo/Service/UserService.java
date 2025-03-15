
package com.example.demo.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Model.User;
import com.example.demo.Repo.UserRepo;

@Service
public class UserService {
	@Autowired
	UserRepo userRepo;

	public boolean isEmailExists(String email) {
		return userRepo.existsByEmail(email.toLowerCase());
	}

	public User saveUser(User user) {
		user.setEmail(user.getEmail().toLowerCase());
		return userRepo.save(user);
	}

}
