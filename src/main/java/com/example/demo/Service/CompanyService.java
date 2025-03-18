package com.example.demo.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Model.Company;
import com.example.demo.Repo.CompanyRepository;

@Service
public class CompanyService {

	@Autowired
	private CompanyRepository companyRepository;

	public List<Company> getAllCompanies() {
		return companyRepository.findAll();
	}

	public Company getCompanyById(Long id) {
		return companyRepository.findById(id).orElse(null);
	}

	public void saveCompany(Company company) {
		companyRepository.save(company);
	}

	public void deleteCompany(Long id) {
		companyRepository.deleteById(id);
	}
}
