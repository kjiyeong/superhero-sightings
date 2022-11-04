/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.superherosightings.service;

import com.sg.superherosightings.dao.SightingDao;
import com.sg.superherosightings.dto.Sighting;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 *
 * @author steve
 */
@Service
public class SightingService {
    
    private final SightingDao sightingDao;
    private final HeroService heroService;
    private final LocationService locationService;
    
    public SightingService(SightingDao sightingDao, HeroService heroService, LocationService locationService) {
        this.sightingDao = sightingDao;
        this.heroService = heroService;
        this.locationService= locationService;
    }
    
    
    public Sighting getSightingById(int id) {
        Sighting sighting = sightingDao.getSightingById(id);
        sighting.setHero(heroService.getHeroById(sighting.getHeroId())); //get super name
        sighting.setLocation(locationService.getLocationById(sighting.getLocationId())); //get location name
        
        return sighting;
    }
    
    public List<Sighting> getAllSightings() {
        List<Sighting> sightings = sightingDao.getAllSightings();
        sightings.forEach(sighting -> sighting.setHero(heroService.getHeroById(sighting.getHeroId()))); //get supers names
        sightings.forEach(sighting -> sighting.setLocation(locationService.getLocationById(sighting.getLocationId()))); //get locations names
        
        return sightings;
    }
    
    public Sighting addSighting(Sighting sighting) {
        Sighting newSighting = sightingDao.addSighting(sighting);
        return newSighting;
    }
    
    public void updateSighting(Sighting sighting) {
        sightingDao.updateSighting(sighting);
    }
    
    public void deleteSightingById(int id) {
        sightingDao.deleteSightingById(id);
    }
    
    public List<Sighting> getSightingsByDate(LocalDate date) {
        List<Sighting> sightings = sightingDao.getSightingsByDate(date);
        return sightings;
    }
    
}
