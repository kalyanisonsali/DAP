package com.example.demo.Controller;

import java.security.Principal;

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
public class UserController 
{
	@Autowired
	UserService userservice;
	
	@Autowired
	UserRepo ur;
	
	@GetMapping("/")
	public String signUp()
	{
		return "login";
	}
	
	@GetMapping("/signup")
	public String signIn()
	{
		return "signup";
	}
	
	@GetMapping("/check-email")
    @ResponseBody
    public String checkEmail(@RequestParam String email) {
        boolean exists = userservice.existsByEmail(email);
        return exists ? "exists" : "available";
    }
	
	@PostMapping("/savedata")
	public String save(@ModelAttribute User u1, Model model) {
	    
	    if (userservice.existsByEmail(u1.getEmail())) {
	        model.addAttribute("errorMessage", "This email is already registered!");
	        return "signup"; // Show error on the same page
	    }

	    userservice.savedata(u1);  // Save user data

	    return "redirect:/";  // Redirect to login page after successful signup
	}
	
	@PostMapping("/checkdata")
	public String checkdata(@RequestParam("t1") String email, 
	                        @RequestParam("t2") String password, 
	                        HttpSession session, 
	                        Model model) {

	    // Check if the user exists in the database
	    User user = ur.findByEmail(email);

	    if (user != null && user.getPassword().equals(password)) { 
	        // Store user details in session
	        session.setAttribute("loggedInUser", user);
	        
	        // Store first name in session for display
	        session.setAttribute("fname", user.getFname());

	        return "redirect:/dash";  // Redirect to dashboard
	    } else {
	        model.addAttribute("error", "Invalid email or password");
	        return "login"; // Redirect back to login page
	    }
	}

	
	
	@GetMapping("/dash")
	public String dash(HttpSession session, Model model) {

	    // Retrieve user from session
	    User user = (User) session.getAttribute("loggedInUser");

	    if (user != null) {
	        model.addAttribute("fname", user.getFname()); // Set first name in model
	    } else {
	        return "redirect:/"; // Redirect to login if session is null
	    }

	    return "dash";  // Load Thymeleaf template for dashboard
	}


}
