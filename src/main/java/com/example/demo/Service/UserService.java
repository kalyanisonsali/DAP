package com.example.demo.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.Model.User;
import com.example.demo.Repo.UserRepo;

@Service
public class UserService
{
	@Autowired
	UserRepo userRepo;

	 public void savedata(User user) {
	        userRepo.save(user);  // Save user to the database
	    }

      public User checkUser(String t1, String t2)
      {
		
		     return userRepo.findByEmailAndPassword(t1, t2);
	  }


      public boolean existsByEmail(String email)
      {
          return userRepo.findByEmail(email) != null;  // Check if email exists
      }

	
}
