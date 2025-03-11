package com.example.demo.Controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.Model.User;
import com.example.demo.Repo.UserRepo;
import com.example.demo.Service.UserService;

@Controller
public class UserController {
	@Autowired
	UserService userservice;

	@Autowired
	UserRepo ur;

	@GetMapping("/")
	public String signUp() {
		return "login";
	}

	@GetMapping("/signup")
	public String signIn() {
		return "signup";
	}

	@PostMapping("/checkdata")
	public String checkdata(@RequestParam("t1") String email, @RequestParam("t2") String password, HttpSession session,
			Model model) {

		User user = ur.findByEmail(email);

		if (user != null && user.getPassword().equals(password)) {
			session.setAttribute("loggedInUser", user);
			session.setAttribute("fname", user.getFname());

			return "redirect:/dash";
		} else {
			model.addAttribute("error", "Invalid email or password");
			return "login";
		}
	}

	@GetMapping("/userDash")
	public String dash(HttpSession session, Model model) {
		User user = (User) session.getAttribute("loggedInUser");

		if (user != null) {
			model.addAttribute("fname", user.getFname());
		} else {
			return "redirect:/";
		}

		return "dash";
	}

	@GetMapping("/check-email")
	@ResponseBody
	public String checkEmail(@RequestParam String email) {
		boolean exists = userservice.existsByEmail(email);
		return exists ? "exists" : "available";
	}

	@PostMapping("/savedata")
	public String save(@ModelAttribute("u1") User u1, @RequestParam String email, Model model) {
		if (userservice.existsByEmail(email)) {
			model.addAttribute("errorMessage", "This email is already registered!");
			return "redirect:/signup";
		}
		userservice.savedata(u1);
		return "redirect:/";
	}

}
