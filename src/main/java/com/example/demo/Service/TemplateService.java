package com.example.demo.Service;

import com.example.demo.Model.template;
import com.example.demo.Repo.TemplateRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TemplateService {

    @Autowired
    private TemplateRepo templaterepo;

    public template saveTemplate(template template) {
        return templaterepo.save(template);
    }

    public List<template> getAllTemplates() {
        return templaterepo.findAll();
    }
}

