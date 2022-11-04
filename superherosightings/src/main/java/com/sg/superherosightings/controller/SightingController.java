/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.superherosightings.controller;

import com.sg.superherosightings.dao.SightingDao;
import com.sg.superherosightings.dto.Hero;
import com.sg.superherosightings.dto.Location;
import com.sg.superherosightings.dto.Sighting;
import com.sg.superherosightings.service.HeroService;
import com.sg.superherosightings.service.LocationService;
import com.sg.superherosightings.service.SightingService;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
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
public class SightingController {
    
    Set<ConstraintViolation<Sighting>> violations = new HashSet<>();
    
    @Autowired
    SightingDao sightingDao;
    @Autowired
    HeroService heroService;
    @Autowired
    LocationService locationService;
    @Autowired
    SightingService sightingService;
    
    //public SightingController(SightingDao sightingDao, HeroService heroService, LocationService locationService, SightingService sightingService){
      //  this.sightingDao = sightingDao;
      //  this.heroService = heroService;
      //  this.locationService = locationService;
      //  this.sightingService = sightingService;
    //}
    
    @GetMapping("sightings") //Go to sightings html page
    public String displaySightings(Model model) {
        List<Sighting> sightings = sightingService.getAllSightings();
        List<Hero> heroes = heroService.getAllHeroes();
        List<Location> locations = locationService.getAllLocations();
        model.addAttribute("sightings", sightings);
        model.addAttribute("heroes", heroes);
        model.addAttribute("locations", locations);

        model.addAttribute("sighting", model.getAttribute("sighting") != null ? model.getAttribute("sighting") : new Sighting());
        model.addAttribute("errors", violations);
        
        return "sightings"; //returning "sightings" means we will need a sightings.html file to push our data to
    }
    
    @PostMapping("addSighting")
    public String addSighting(@Valid Sighting sighting, BindingResult result, HttpServletRequest request, Model model) {        
        String heroId = request.getParameter("hero_id");
        String locationId = request.getParameter("location_id");        
        sighting.setHeroId(Integer.parseInt(heroId));
        sighting.setLocationId(Integer.parseInt(locationId)); 
        
        Validator validate = Validation.buildDefaultValidatorFactory().getValidator();
        violations = validate.validate(sighting);

        if(violations.isEmpty()) {
            //sightingDao.addSighting(sighting);
    }
        
        if(result.hasErrors()){    
            return displaySightings(model);
        }
        sightingDao.addSighting(sighting);        
        return "redirect:/sightings";
    }
    
    @GetMapping("detailSighting") //Go to detailSighting html page
    public String detailSighting(Integer id, Model model) {
        Sighting sighting = sightingService.getSightingById(id);
        model.addAttribute("sighting", sighting);
        return "detailSighting";
    }
    
    @GetMapping("editSighting") //Go to editSighting html page
    public String editSighting(Integer id, Model model) {
        List<Hero> heroes = heroService.getAllHeroes();
        List<Location> locations = locationService.getAllLocations();
        Sighting sighting = sightingService.getSightingById(id);
        
        if(model.getAttribute("sighting") != null) {
            ((Sighting) model.getAttribute("sighting")).setHeroId(sighting.getHeroId());
            ((Sighting) model.getAttribute("sighting")).setLocationId(sighting.getLocationId());
        }
        model.addAttribute("sighting", model.getAttribute("sighting") != null ? model.getAttribute("sighting") : sighting);
        model.addAttribute("heroes", heroes);
        model.addAttribute("locations", locations); 
        model.addAttribute("currentDate", sighting.date);            
        model.addAttribute("sighting", sighting);
        return "editSighting";
    }
    
    @PostMapping("editSighting")
    public String performEditSighting(@Valid Sighting sighting, BindingResult result, HttpServletRequest request, Model model) { 
        String heroId = request.getParameter("hero_id");
        String locationId = request.getParameter("location_id");        
        sighting.setHeroId(Integer.parseInt(heroId));
        sighting.setLocationId(Integer.parseInt(locationId)); 
        
        if(result.hasErrors()){  
            return editSighting(sighting.getId(), model);
        } 
        
        sightingService.updateSighting(sighting);
        return "redirect:/detailSighting?id=" + sighting.getId();
    }
    
    @GetMapping("displayDeleteSighting") //Go to deleteSighting html page for confirmation
    public String displayDeleteSighting(Integer id, Model model) { 
        Sighting sighting = sightingService.getSightingById(id);
        model.addAttribute("sighting", sighting);
        return "deleteSighting";
    }
    
    @GetMapping("deleteSighting")
    public String deleteSighting(Integer id) {
        sightingService.deleteSightingById(id);
        return "redirect:/sightings";
    }
    
    @GetMapping("/") //Go to index html page
    public String recentSightings(Model model) {
        List<Sighting> sightings = sightingService.getAllSightings();      
        
        List<Sighting> recentSightings = sightings.stream()
                .sorted(Comparator.comparing(Sighting::getDate).reversed()) //order by date from most recent to oldest
                .limit(10) //get the 10 first sightings
                .collect(Collectors.toList());        

        model.addAttribute("sightings", recentSightings);
        
        return "index.html"; //returning "sightings" means we will need a sightings.html file to push our data to
    }
}
