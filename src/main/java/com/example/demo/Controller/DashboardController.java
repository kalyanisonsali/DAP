package com.example.demo.Controller;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
//@RequestMapping("/dashboard")
public class DashboardController {

	@GetMapping("/dash")
	public String dashboard(Model model, OAuth2AuthenticationToken authenticationToken) {
		if (authenticationToken != null) {
			OAuth2User user = authenticationToken.getPrincipal();
			System.out.println("User attributes: " + user.getAttributes()); // Debugging

			model.addAttribute("name", user.getAttribute("name")); // Full name
			model.addAttribute("email", user.getAttribute("email")); // Email
			model.addAttribute("picture", user.getAttribute("picture")); // Profile Picture (For Google)
			model.addAttribute("avatar_url", user.getAttribute("avatar_url")); // GitHub Avatar
		}
		return "dash";
	}
}
