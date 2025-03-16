//package com.example.demo.Controller;
//
//import java.security.Principal;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//
//@Controller
//public class FeedbackController {
//
//	@GetMapping("/feedback")
//	public String showFeedbackPage(Model model, Principal principal) {
//		if (principal != null) {
//			model.addAttribute("fname", principal.getName());
//		}
//		return "feedback";
//	}
//
//}
