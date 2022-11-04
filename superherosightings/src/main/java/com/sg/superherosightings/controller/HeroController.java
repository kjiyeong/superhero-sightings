/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.superherosightings.controller;

import com.sg.superherosightings.dao.HeroDao;
import com.sg.superherosightings.dto.Hero;
import com.sg.superherosightings.dto.Organization;
import com.sg.superherosightings.dto.Power;
import com.sg.superherosightings.service.HeroService;
import com.sg.superherosightings.service.OrganizationService;
import com.sg.superherosightings.service.PowerService;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author steve
 */
@Controller
public class HeroController {
   
   Set<ConstraintViolation<Hero>> violations = new HashSet<>();
   
   //@Autowired
   //HeroDao heroDao;
   
   @Autowired
   OrganizationService organizationService;
   @Autowired
   PowerService powerService;
   @Autowired
   HeroService heroService;
   
   //public HeroController(OrganizationService organizationService, PowerService powerService, HeroService heroService) {
    //    this.organizationService = organizationService;
    //    this.powerService = powerService;    
    //    this.heroService = heroService;    
    //}
   
   @GetMapping("heroes") //Go to heroes html page
    public String displayHeroes(Model model) {
        List<Hero> heroes = heroService.getAllHeroes();
        List<Power> powers = powerService.getAllPowers();
        List<Organization> organizations = organizationService.getAllOrganizations();
        model.addAttribute("heroes", heroes);
        model.addAttribute("powers", powers);
        model.addAttribute("organizations", organizations);
        model.addAttribute("hero", model.getAttribute("hero") != null ? model.getAttribute("hero") : new Hero());
        model.addAttribute("errors", violations);
        
        return "heroes"; //returning "heroes" means we will need a heroes.html file to push our data to
    }
    
    @PostMapping("addHero")
    public String addHero(@Valid Hero hero, BindingResult result, @RequestParam("heroImageToSave") MultipartFile file, HttpServletRequest request, Model model, RedirectAttributes redirect) throws IOException {  
        List<String> powerIds = Arrays.asList(Optional.ofNullable(request.getParameterValues("power_id")).orElse(new String[0])); //if the received parameter is null, create an empty list
        List<String> organizationIds = Arrays.asList(Optional.ofNullable(request.getParameterValues("organization_id")).orElse(new String[0]));
        
        Validator validate = Validation.buildDefaultValidatorFactory().getValidator();
        violations = validate.validate(hero);
        
        if(violations.isEmpty()) {
         //   heroDao.addHero(hero);
        }
        
        if(result.hasErrors()){  
            List<Hero> heroes = heroService.getAllHeroes();
            model.addAttribute("heroes", heroes); //to fill the listing
            return displayHeroes(model);
        }
        
        
        heroService.addHero(hero, powerIds, organizationIds, file.getBytes());        
        return "redirect:/heroes";
    }
    
    @GetMapping("detailHero") //Go to detailHero html page
    public String heroDetail(Integer id, Model model) {
        Hero hero = heroService.getHeroById(id);
        model.addAttribute("hero", hero);
        return "detailHero";
    }
    
    @GetMapping("editHero") //Go to editHero html page
    public String editHero(Integer id, Model model) {
        Hero hero = heroService.getHeroById(id);
        List<Power> powers = powerService.getAllPowers();
        List<Organization> organizations = organizationService.getAllOrganizations();
        
        if(model.getAttribute("hero") != null) {
            ((Hero) model.getAttribute("hero")).setPowers(hero.getPowers());
            ((Hero) model.getAttribute("hero")).setOrganizations(hero.getOrganizations());
        }
        model.addAttribute("hero", model.getAttribute("hero") != null ? model.getAttribute("hero") : hero);
        model.addAttribute("powers", powers);
        model.addAttribute("organizations", organizations);
        return "editHero";
    }
    
    @PostMapping("editHero")
    public String performEditHero(@Valid Hero hero, BindingResult result, HttpServletRequest request, Model model) {        
        List<String> powerIds = Arrays.asList(Optional.ofNullable(request.getParameterValues("power_id")).orElse(new String[0]));
        List<String> organizationIds = Arrays.asList(Optional.ofNullable(request.getParameterValues("organization_id")).orElse(new String[0]));
        
        if(result.hasErrors()){  
            return editHero(hero.getId(), model);
        } 
        
        heroService.updateHero(hero, powerIds, organizationIds);

        return "redirect:/detailHero?id=" + hero.getId();
    }
    
    @GetMapping("displayDeleteHero") //Go to deleteHero html page for confirmation
    public String displayDeleteHero(Integer id, Model model) { 
        Hero hero = heroService.getHeroById(id);
        model.addAttribute("hero", hero);
        return "deleteHero";
    }
    
    @GetMapping("deleteHero")
    public String deleteSuper(Integer id) {
        heroService.deleteHeroById(id);
        return "redirect:/heroes";
    }
    
    @GetMapping("heroes/{id}/image")
    public void renderHeroImage(@PathVariable String id, HttpServletResponse response) throws IOException {
        Hero hero = heroService.getHeroById(Integer.parseInt(id));
        
        response.setContentType("image/jpeg");
        InputStream is = new ByteArrayInputStream(hero.getHeroImage());
        IOUtils.copy(is, response.getOutputStream());
    }
}
