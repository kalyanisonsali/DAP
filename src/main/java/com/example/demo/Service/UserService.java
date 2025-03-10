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

	public void savedata(User u1)
	{
		userRepo.save(u1);
		
	}

	public boolean existsByEmail(String email) {
		// TODO Auto-generated method stub
		return true;
	}

}
