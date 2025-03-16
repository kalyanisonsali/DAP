package com.example.demo.Controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

	@GetMapping("/dash")
	public String dashboard(Model model, Principal principal) {
		model.addAttribute("currentPage", "profile");

		if (principal != null) {
			model.addAttribute("username", principal.getName());
			
		}
		return "userDash";
	}
}
