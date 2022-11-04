/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.superherosightings.service;

import com.sg.superherosightings.dao.HeroDao;
import com.sg.superherosightings.dto.Hero;
import com.sg.superherosightings.dto.Location;
import com.sg.superherosightings.dto.Organization;
import com.sg.superherosightings.dto.Power;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 *
 * @author steve
 */
@Service
public class HeroService {
    
    private final HeroDao heroDao;
    
    private final PowerService powerService;
    private final OrganizationService organizationService;
    
    public HeroService(HeroDao heroDao, PowerService powerService, OrganizationService organizationService){
        this.powerService = powerService;
        this.organizationService = organizationService;
        this.heroDao = heroDao;
    }
    
    public Hero getHeroById(int id) {
        Hero hero = heroDao.getHeroById(id);
        return hero;
    }
    
    public List<Hero> getAllHeroes() {
        List<Hero> heroes = heroDao.getAllHeroes();
        return heroes;
    }    
    
    public Hero addHero(Hero heroVillain, List<String> powerIds, List<String> organizationIds, byte[] heroImage) {
        List<Power> powers = new ArrayList<>();
        for(String powerId : powerIds) {
            powers.add(powerService.getPowerById(Integer.parseInt(powerId)));
        }
        
        List<Organization> organizations = new ArrayList<>();
        for(String organizationId : organizationIds) {
           organizations.add(organizationService.getOrganizationById(Integer.parseInt(organizationId)));
        }
        
        heroVillain.setPowers(powers);
        heroVillain.setOrganizations(organizations);
        heroVillain.setHeroImage(heroImage);
        
        Hero hero = heroDao.addHero(heroVillain);
        return hero;
    }
    
    public void updateHero(Hero heroVillain, List<String> powerIds, List<String> organizationIds) {
        List<Power> powers = new ArrayList<>();
        for(String powerId : powerIds) {
            powers.add(powerService.getPowerById(Integer.parseInt(powerId)));
        }
        
        List<Organization> organizations = new ArrayList<>();
        for(String organizationId : organizationIds) {
           organizations.add(organizationService.getOrganizationById(Integer.parseInt(organizationId)));
        }
        
        heroVillain.setPowers(powers);
        heroVillain.setOrganizations(organizations);
        
        heroDao.updateHero(heroVillain);    
    }
    
    public void deleteHeroById(int id) {
        heroDao.deleteHeroById(id);
    }
    
    public List<Hero> getHeroesByLocation(Location location) {
        List<Hero> supers = heroDao.getHeroesByLocation(location);
        return supers;
    }
    
    public List<Hero> getHerosByOrganization(Organization organization) {
        List<Hero> heroes = heroDao.getHeroesByOrganization(organization);
        return heroes;
    }
    
    public List<Location> getLocationsForHero(int id) {
        List<Location> locations = heroDao.getLocationsForHero(id);
        return locations;
    }
    
    public List<Power> getPowersForHero(int id) {
        List<Power> powers = heroDao.getPowersForHero(id);
        return powers;
    }
    
    public List<Organization> getOrganizationsForHero(int id) {
        List<Organization> organizations = heroDao.getOrganizationsForHero(id);
        return organizations;
    }
    
    public void removePowerForHero(int heroId, int powerId) {
        heroDao.removePowerForHero(heroId, powerId);
    }
    
    public void removeOrganizationForHero(int heroId, int organizationId) {
        heroDao.removeOrganizationForHero(heroId, organizationId);
    }
}
