package com.example.demo.Controller;

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
    @ResponseBody  // This allows returning plain text
    public String checkEmail(@RequestParam String email) {
        boolean exists = userservice.existsByEmail(email);
        return exists ? "exists" : "available";
    }
	
	@PostMapping("/savedata")
	public String save(@ModelAttribute("u1") User u1,@RequestParam String email, Model model)
	{
		 if (userservice.existsByEmail(email)) {
	            model.addAttribute("errorMessage", "This email is already registered!");
	            return "redirect:/signup"; // Return back to the JSP page with an error message
	        }
		userservice.savedata(u1);
		return "redirect:/signup";
	}
	
	

}
