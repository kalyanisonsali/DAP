package com.example.demo.Controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.Model.PasswordResetToken;
import com.example.demo.Model.User;
import com.example.demo.Repo.PasswordResetTokenRepository;
import com.example.demo.Repo.UserRepo;
import com.example.demo.Service.EmailService;
import com.example.demo.Service.UserService;

@Controller
public class UserController {
	@Autowired
	UserService userservice;

	@Autowired
	UserRepo ur;

	@Autowired
	private PasswordResetTokenRepository tokenRepository;

	@Autowired
	private EmailService emailService;

	@GetMapping("/")
	public String signIn(HttpSession session) {
		if (session.getAttribute("loggedInUser") != null) {
			return "redirect:/dashboard";
		}
		session.removeAttribute("success");
		session.removeAttribute("error");
		return "login";
	}

	@GetMapping("/signup")
	public String signUp() {
		return "signup";
	}

	@GetMapping("/create-project")
	public String createProject(Model model) {
		model.addAttribute("currentPage", "newProject");
		return "createProject";
	}

	@GetMapping("/view-projects")
	public String viewProjects(Model model) {
		model.addAttribute("currentPage", "viewProjects");
		return "viewProjects";
	}

	@GetMapping("/feedback")
	public String feedbackPage(HttpSession session, Model model) {
		User user = (User) session.getAttribute("loggedInUser");

		if (user != null) {
			model.addAttribute("fname", user.getFname());
		} else {
			model.addAttribute("fname", "Guest");
		}
		model.addAttribute("currentPage", "feedback");
		return "feedback";
	}

	@GetMapping("/logout")
	public String logout(HttpSession session, Model model) {
		session.invalidate();
		model.addAttribute("currentPage", "logout");
		return "redirect:/";
	}

	@PostMapping("/checkdata")
	public String checkLogin(@RequestParam("email") String email, @RequestParam("password") String password,
			HttpSession session, Model model, RedirectAttributes redirectAttributes) {

		System.out.println("Received login request for email: " + email);

		Integer loginAttempts = (Integer) session.getAttribute("loginAttempts");
		if (loginAttempts == null)
			loginAttempts = 0;

		if (loginAttempts >= 5) {
			redirectAttributes.addFlashAttribute("error", "Too many failed login attempts. Try again later.");
			return "redirect:/";
		}

		Optional<User> userOptional = ur.findByEmail(email);

		if (userOptional.isPresent()) {
			User user = userOptional.get();
			if (user.getPassword().equals(password)) { // No encryption used
				session.setAttribute("loggedInUser", user);
				session.setAttribute("fname", user.getFname());
				session.setAttribute("loginAttempts", 0);
				System.out.println("Login successful for: " + email);
				redirectAttributes.addFlashAttribute("success", "Login successful!");
				return "redirect:/userDash";
			} else {
				System.out.println("Incorrect password for: " + email);
				redirectAttributes.addFlashAttribute("error", "Invalid email or password.");
			}
		} else {
			System.out.println("User not found: " + email);
			redirectAttributes.addFlashAttribute("error", "Invalid email or password.");
		}
		session.setAttribute("loginAttempts", loginAttempts + 1);
		return "redirect:/";
	}

	@GetMapping("/userDash")
	public String dashboard(HttpSession session, Model model) {
		User user = (User) session.getAttribute("loggedInUser");

		if (user != null) {
			model.addAttribute("fname", user.getFname());
			model.addAttribute("currentPage", "userDash");
			return "dash";
		} else {
			return "redirect:/";
		}
	}

	@GetMapping("/check-email")
	@ResponseBody
	public Map<String, String> checkEmail(@RequestParam String email) {
		Map<String, String> response = new HashMap<>();
		response.put("status", userservice.isEmailExists(email) ? "exists" : "available");
		return response;
	}

	@PostMapping("/savedata")
	@ResponseBody
	public Map<String, String> saveUser(@RequestParam String fname, @RequestParam String lname,
			@RequestParam String email, @RequestParam String password) {
		Map<String, String> response = new HashMap<>();

		if (userservice.isEmailExists(email)) {
			response.put("status", "error");
			response.put("message", "Email already exists");
			return response;
		}

		User user = new User();
		user.setFname(fname);
		user.setLname(lname);
		user.setEmail(email);
		user.setPassword(password);

		userservice.saveUser(user);
		response.put("status", "success");
		return response;
	}

	@GetMapping("/forgot-password")
	public String forgotPasswordPage() {
		return "forgot-password"; // This will show the password reset page
	}

	@PostMapping("/forgot-password")
	public String processForgotPassword(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
		Optional<User> userOptional = ur.findByEmail(email);

		if (userOptional.isPresent()) {
			User user = userOptional.get();

			String token = UUID.randomUUID().toString();
			PasswordResetToken resetToken = new PasswordResetToken();
			resetToken.setToken(token);
			resetToken.setUser(user);
			resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(30)); // Token valid for 30 minutes

			tokenRepository.save(resetToken);

			String resetLink = "http://localhost:8080/reset-password?token=" + token;
			emailService.sendEmail(user.getEmail(), "Password Reset Request",
					"Click the link to reset your password: " + resetLink);

			redirectAttributes.addFlashAttribute("success", "Password reset link sent to your email.");
		} else {
			redirectAttributes.addFlashAttribute("error", "Email not found.");
		}

		return "redirect:/forgot-password";
	}

	@GetMapping("/reset-password")
	public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
		Optional<PasswordResetToken> tokenOptional = tokenRepository.findByToken(token);

		if (tokenOptional.isPresent() && !tokenOptional.get().isExpired()) {
			model.addAttribute("token", token);
			return "reset-password";
		} else {
			return "redirect:/forgot-password?error=Invalid or expired token";
		}
	}

	@PostMapping("/reset-password")
	public String processResetPassword(@RequestParam("token") String token, @RequestParam("password") String password,
			RedirectAttributes redirectAttributes) {
		Optional<PasswordResetToken> tokenOptional = tokenRepository.findByToken(token);

		if (tokenOptional.isPresent() && !tokenOptional.get().isExpired()) {
			PasswordResetToken resetToken = tokenOptional.get();
			User user = resetToken.getUser();

			user.setPassword(password);
			ur.save(user);

			tokenRepository.delete(resetToken);

			redirectAttributes.addFlashAttribute("success", "Password reset successful. You can now login.");
			return "redirect:/";
		} else {
			redirectAttributes.addFlashAttribute("error", "Invalid or expired token.");
			return "redirect:/forgot-password";
		}
	}

}