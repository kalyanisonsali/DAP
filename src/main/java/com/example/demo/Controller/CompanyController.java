package com.example.demo.Controller;

import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.Model.Company;
import com.example.demo.Model.User;
import com.example.demo.Service.CompanyService;

@Controller
@RequestMapping("/company-management")
public class CompanyController {

	@Autowired
	private CompanyService companyService;

	@GetMapping
	public String showCompanyManagement(HttpSession session, Model model) {
		User user = (User) session.getAttribute("loggedInUser");

		if (user != null) {
			model.addAttribute("fname", user.getFname());

		} else {
			model.addAttribute("fname", "Guest");
		}

		model.addAttribute("companies",
				companyService.getAllCompanies() != null ? companyService.getAllCompanies() : new ArrayList<>());
		model.addAttribute("company", new Company());
		model.addAttribute("lobList", Arrays.asList("Software", "Finance", "Healthcare", "Education"));

		model.addAttribute("currentPage", "company-management");
		return "company-management";
	}

	@GetMapping("/company-form")
	public String showCompanyForm(Model model) {
		model.addAttribute("company", new Company());
		return "companyForm"; // Ensure "companyForm.html" exists in /templates
	}

	@GetMapping("/list")
	public String getAllCompanies(Model model) {
		model.addAttribute("companies", companyService.getAllCompanies());
		model.addAttribute("company", new Company());
		model.addAttribute("lobList", Arrays.asList("Software", "Finance", "Healthcare", "Education"));
		return "company-management";
	}

	@PostMapping("/save-company")
	public String saveCompany(@ModelAttribute Company company) {
		companyService.saveCompany(company);
		return "redirect:/company-management";
	}

	@GetMapping("/edit/{id}")
	public String editCompany(@PathVariable Long id, Model model) {
		model.addAttribute("company", companyService.getCompanyById(id));
		model.addAttribute("companies", companyService.getAllCompanies());
		model.addAttribute("lobList", Arrays.asList("Software", "Finance", "Healthcare", "Education"));
		return "company-management";
	}

	@GetMapping("/delete/{id}")
	public String deleteCompany(@PathVariable Long id) {
		companyService.deleteCompany(id);
		return "redirect:/company-management";
	}
}
