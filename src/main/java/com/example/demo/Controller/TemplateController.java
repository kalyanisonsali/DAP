package com.example.demo.Controller;

import com.example.demo.Model.User;
import com.example.demo.Model.template;
import com.example.demo.Service.TemplateService;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class TemplateController 
{
	@Autowired
	TemplateService templateService;
	
	@GetMapping("/template-management")
    public String showTemplatePage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        
        if (user == null) {
            return "redirect:/";
        }

        // Create new template and assign logged-in user as creator
        template template = new template();
        template.setCreated_by(user.getEmail()); // Auto-fill email

        model.addAttribute("template", template); // Attach template to model
        model.addAttribute("fname", user.getFname());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("templates", templateService.getAllTemplates());

        return "template-management";
    }


    @PostMapping("/saveTemplate")
    public String saveTemplate(@ModelAttribute template template, RedirectAttributes redirectAttributes,Model model) {
    	templateService.saveTemplate(template);
        redirectAttributes.addFlashAttribute("message", "Template saved successfully!");
        List<template> templates = templateService.getAllTemplates();
        model.addAttribute("templates", templates);
        

        return "redirect:/template-management"; 
    }

    
}
