package com.example.demo.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.Model.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

}
