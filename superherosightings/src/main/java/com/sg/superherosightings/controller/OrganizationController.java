/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.superherosightings.controller;

import com.sg.superherosightings.dao.OrganizationDao;
import com.sg.superherosightings.dto.Hero;
import com.sg.superherosightings.dto.Organization;
import com.sg.superherosightings.dto.Power;
import com.sg.superherosightings.service.HeroService;
import com.sg.superherosightings.service.OrganizationService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 *
 * @author steve
 */
@Controller
public class OrganizationController {
    
    Set<ConstraintViolation<Organization>> violations = new HashSet<>();
    
    @Autowired
    OrganizationDao organizationDao;
    @Autowired
    OrganizationService organizationService;
    @Autowired
    HeroService heroService;
    
    //public OrganizationController(OrganizationDao organizationDao, OrganizationService organizationService, HeroService heroService){
      //  this.organizationDao = organizationDao;
      //  this.organizationService = organizationService;
      //  this.heroService = heroService;
    //}
    
    @GetMapping("organizations") //Go to organizations html page
    public String displayOrganizations(Model model) {
        List<Organization> organizations = organizationDao.getAllOrganizations();          
        model.addAttribute("organizations", organizations);
        model.addAttribute("organization", model.getAttribute("organization") != null ? model.getAttribute("organization") : new Organization());
        model.addAttribute("errors", violations);
        
        return "organizations"; //returning "organizations" means we will need a organizations.html file to push our data to
    }
    
    @PostMapping("addOrganization")
    public String addOrganization(@Valid Organization organization, BindingResult result, Model model) {
        
        Validator validate = Validation.buildDefaultValidatorFactory().getValidator();
        violations = validate.validate(organization);

        if(violations.isEmpty()) {
            //organizationDao.addOrganization(organization);
    }
                 
        if(result.hasErrors()) {
            List<Organization> organizations = organizationService.getAllOrganizations();
            model.addAttribute("organizations", organizations);
            return displayOrganizations(model);            
        } else {
            organizationService.addOrganization(organization);
            return "redirect:/organizations";            
        }
        
    }
    
    @GetMapping("detailOrganization") //Go to detailOrganization html page
    public String detailOrganization(Integer id, Model model) {
        Organization organization = organizationService.getOrganizationById(id);
        List<Hero> membersByOrganization = heroService.getHerosByOrganization(organization);
        model.addAttribute("organization", organization);
        model.addAttribute("members", membersByOrganization);
        return "detailOrganization";
    }
    
    @GetMapping("displayDeleteOrganization") //Go to deleteOrganization html page for confirmation
    public String displayDeleteOrganization(Integer id, Model model) { 
        Organization organization = organizationService.getOrganizationById(id);
        model.addAttribute("organization", organization);
        return "deleteOrganization";
    }
    
    @GetMapping("deleteOrganization")
    public String deleteOrganization(Integer id) {
        organizationService.deleteOrganizationById(id);
        return "redirect:/organizations";
    }  
        
     @GetMapping("editOrganization") //Go to editOrganization html page
        public String editOrganization(Integer id, Model model) {
        Organization organization = organizationService.getOrganizationById(id);
        model.addAttribute("organization", organization);
        return "editOrganization";
    }
    
    @PostMapping("editOrganization")
    public String performEditOrganization(@Valid Organization organization, BindingResult result, Model model) { 
        organizationService.updateOrganization(organization);
        
        if(result.hasErrors()){  
            List<Organization> organizations = organizationService.getAllOrganizations();
            model.addAttribute("organizations", organizations); //to fill the listing
            return "editOrganization";
        } 
        
        return "redirect:/detailOrganization?id=" + organization.getId();
    }
    
}
