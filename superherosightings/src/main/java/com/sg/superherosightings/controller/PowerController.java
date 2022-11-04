/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.superherosightings.controller;

import com.sg.superherosightings.dao.PowerDao;
import com.sg.superherosightings.dto.Power;
import com.sg.superherosightings.service.PowerService;
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
public class PowerController {
    
    Set<ConstraintViolation<Power>> violations = new HashSet<>();
    
    @Autowired
    PowerDao powerDao;
    @Autowired
    PowerService powerService;
    
    //public PowerController(PowerDao powerDao, PowerService powerService){
      //  this.powerDao = powerDao;
      //  this.powerService = powerService;
    //}
    
    @GetMapping("powers") //Go to powers html page
    public String displayPowers(Model model) {
        List<Power> powers = powerDao.getAllPowers();
        model.addAttribute("powers", powers);
        
        model.addAttribute("power", model.getAttribute("power") != null ? model.getAttribute("power") : new Power());
        model.addAttribute("errors", violations);
        
        return "powers"; //returning "powers" means we will need a powers.html file to push our data to
    }
    
    @PostMapping("addPower")
    public String addPower(@Valid Power power, BindingResult result, Model model) {
        
        Validator validate = Validation.buildDefaultValidatorFactory().getValidator();
        violations = validate.validate(power);

        if(violations.isEmpty()) {
            //powerDao.addPower(power);
    }
        
        if(result.hasErrors()) {
            List<Power> powers = powerService.getAllPowers();
            model.addAttribute("powers", powers);
            return displayPowers(model);            
        }
        
        powerService.addPower(power);        
        return "redirect:/powers";
    }
    
    @GetMapping("detailPower") //Go to detailPower html page
    public String detailPower(Integer id, Model model) {
        Power power = powerService.getPowerById(id);
        model.addAttribute("power", power);
        return "detailPower";
    }
    
    @GetMapping("editPower") //Go to editPower html page
        public String editPower(Integer id, Model model) {
        Power power = powerService.getPowerById(id);
        model.addAttribute("power", power);
        return "editPower";
    }
    
    @PostMapping("editPower")
    public String performEditPower(@Valid Power power, BindingResult result, Model model) {         
        if(result.hasErrors()) {
            return "editPower";
        }
        
        powerService.updatePower(power);
        return "redirect:/detailPower?id=" + power.getId();
    }
    
    @GetMapping("displayDeletePower") //Go to deletePower html page for confirmation
    public String displayDeletePower(Integer id, Model model) { 
        Power power = powerService.getPowerById(id);
        model.addAttribute("power", power);
        return "deletePower";
    }
    
    @GetMapping("deletePower")
    public String deletePower(Integer id) {
        powerService.deletePowerById(id);
        return "redirect:/powers";
    } 
}
