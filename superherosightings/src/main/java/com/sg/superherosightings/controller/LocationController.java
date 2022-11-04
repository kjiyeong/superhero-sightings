/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.superherosightings.controller;

import com.sg.superherosightings.dao.LocationDao;
import com.sg.superherosightings.dto.Hero;
import com.sg.superherosightings.dto.Location;
import com.sg.superherosightings.dto.Organization;
import com.sg.superherosightings.service.HeroService;
import com.sg.superherosightings.service.LocationService;
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
public class LocationController {
    
    Set<ConstraintViolation<Location>> violations = new HashSet<>();
    
    @Autowired
    LocationDao locationDao;
    @Autowired
    LocationService locationService;
    @Autowired
    HeroService heroService;
    
    //public LocationController(LocationDao locationDao, LocationService locationService, HeroService heroService){
      //  this.locationDao = locationDao;
      //  this.locationService = locationService;
      //  this.heroService = heroService;
    //}
    
     @GetMapping("locations") //Go to locations html page
    public String displayLocations(Model model) {
        List<Location> locations = locationService.getAllLocations();
        model.addAttribute("locations", locations);
        
        model.addAttribute("location", model.getAttribute("location") != null ? model.getAttribute("location") : new Location());
        model.addAttribute("errors", violations);
        
        return "locations"; //returning "locations" means we will need a locations.html file to push our data to
    }
    
    @PostMapping("addLocation")
    public String addLocation(@Valid Location location, BindingResult result, Model model) { 
        if(result.hasErrors()) {
            List<Location> locations = locationService.getAllLocations();
            model.addAttribute("locations", locations);
            return displayLocations(model);            
        }
        
        Validator validate = Validation.buildDefaultValidatorFactory().getValidator();
        violations = validate.validate(location);

        if(violations.isEmpty()) {
            //locationDao.addOrganization(organization);
    }
        
        locationService.addLocation(location);        
        return "redirect:/locations";
    }
    
    @GetMapping("detailLocation") //Go to detailLocation html page
    public String detailLocation(Integer id, Model model) {
        Location location = locationService.getLocationById(id);
        List<Hero> heroesByLocation = heroService.getHeroesByLocation(location);
        model.addAttribute("location", location);
        model.addAttribute("heroes", heroesByLocation);
        return "detailLocation";
    }
    
    @GetMapping("editLocation") //Go to editLocation html page
        public String editLocation(Integer id, Model model) {
        Location location = locationService.getLocationById(id);
        
        model.addAttribute("location", model.getAttribute("location") != null ? model.getAttribute("location") : location);
        
        return "editLocation";
    }
    
    @PostMapping("editLocation")
    public String performEditLocation(@Valid Location location, BindingResult result, Model model) { 
        if(result.hasErrors()) {
            return editLocation(location.getId(), model);            
        }
        
        locationService.updateLocation(location);
        return "redirect:/detailLocation?id=" + location.getId();
    }
    
     @GetMapping("displayDeleteLocation") //Go to deleteLocation html page for confirmation
    public String displayDeleteLocation(Integer id, Model model) { 
        Location location = locationService.getLocationById(id);
        model.addAttribute("location", location);
        return "deleteLocation";
    }
    
    @GetMapping("deleteLocation")
    public String deleteLocation(Integer id) {
        locationService.deleteLocationById(id);
        return "redirect:/locations";
    }  
}
