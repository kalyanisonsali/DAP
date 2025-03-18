
package com.example.demo.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Model.PasswordResetToken;
import com.example.demo.Model.User;
import com.example.demo.Repo.PasswordResetTokenRepository;
import com.example.demo.Repo.UserRepo;
import java.sql.Timestamp;

@Service
public class UserService {
	@Autowired
	UserRepo userRepo;
	
	 @Autowired
	    private PasswordResetTokenRepository tokenRepository;

	public boolean isEmailExists(String email) {
		return userRepo.existsByEmail(email.toLowerCase());
	}

	public User saveUser(User user) {
		user.setEmail(user.getEmail().toLowerCase());
		return userRepo.save(user);
	}

	public void createPasswordResetToken(User user, String token) {
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setEmail(user.getEmail());
        resetToken.setExpiryDate(new Timestamp(System.currentTimeMillis() + 1000L * 60 * 30)); // 30 mins
        tokenRepository.save(resetToken);
    }

	public Optional<User> findByToken(String token) {
        Optional<PasswordResetToken> resetToken = tokenRepository.findByToken(token);
        return resetToken.flatMap(t -> userRepo.findByEmail(t.getEmail()));
    }

    public void updatePassword(User user, String newPassword) {
        user.setPassword(newPassword); // Store plain text (not secure, should hash)
        userRepo.save(user);
    }

	public Optional<User> findByEmail(String email) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
