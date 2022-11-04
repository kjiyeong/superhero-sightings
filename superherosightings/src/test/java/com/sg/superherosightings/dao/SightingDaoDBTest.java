/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.superherosightings.dao;

import com.sg.superherosightings.dto.Hero;
import com.sg.superherosightings.dto.Location;
import com.sg.superherosightings.dto.Sighting;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *
 * @author steve
 */
@SpringBootTest
public class SightingDaoDBTest {
    
    @Autowired
    LocationDao locationDao;
    
    @Autowired
    SightingDao sightingDao;
            
    @Autowired
    HeroDao heroDao;
    
    public SightingDaoDBTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
        
        List<Location> locations = locationDao.getAllLocations();
        for(Location location : locations) {
            locationDao.deleteLocationById(location.getId());
        }
        
        List<Sighting> sightings = sightingDao.getAllSightings();
        for(Sighting sighting : sightings) {
            sightingDao.deleteSightingById(sighting.getId());
        }
        
        List<Hero> heroes = heroDao.getAllHeroes();
        for(Hero heroVillain : heroes) {
            heroDao.deleteHeroById(heroVillain.getId());
        }
    }
    
    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testAddAndGetSighting() {
        Location location = new Location(); //Create a location
        location.setName("Test Location Name");
        location.setDescription("Test Location Description");
        location.setAddress("Test Location Address");
        location.setLatitude(52.143602);
        location.setLongitude(-116.554823);
        location = locationDao.addLocation(location);
        List<Location> locations = new ArrayList<>();
        locations.add(location);

        Hero heroVillain = new Hero(); //Create a hero
        heroVillain.setName("Test Hero Name");
        heroVillain.setDescription("Test Hero Description");        
        heroVillain.setLocations(locations); //add locations list
        heroVillain = heroDao.addHero(heroVillain); //save hero
        
        Sighting sighting = new Sighting(); //Create a sighting
        sighting.setDate(LocalDate.now());
        sighting.setLocationId(location.getId());
        sighting.setHeroId(heroVillain.getId());        
        sighting = sightingDao.addSighting(sighting); 
        
        Sighting sightingFromDao = sightingDao.getSightingById(sighting.getId()); //get the saved sighting from db
        assertEquals(sighting, sightingFromDao);        
    }
    
    @Test
    public void testGetAllSightings() {
        Location location = new Location(); //Create a location
        location.setName("Test Location Name");
        location.setDescription("Test Location Description");
        location.setAddress("Test Location Address");
        location.setLatitude(52.143602);
        location.setLongitude(-116.554823);
        location = locationDao.addLocation(location);
        List<Location> locations = new ArrayList<>();
        locations.add(location);

        Hero heroVillain = new Hero(); //Create a super
        heroVillain.setName("Test Hero Name");
        heroVillain.setDescription("Test Hero Description");        
        heroVillain.setLocations(locations); //add locations list
        heroVillain = heroDao.addHero(heroVillain); //save hero
        
        Sighting sighting1 = new Sighting(); //Create sighting1
        sighting1.setDate(LocalDate.now().minusDays(1)); //Set date to yesterday
        sighting1.setLocationId(location.getId());
        sighting1.setHeroId(heroVillain.getId());        
        sighting1 = sightingDao.addSighting(sighting1); 
        
        Sighting sighting2 = new Sighting(); //Create sighting2
        sighting2.setDate(LocalDate.now());
        sighting2.setLocationId(location.getId());
        sighting2.setHeroId(heroVillain.getId());        
        sighting2 = sightingDao.addSighting(sighting2);
        
        List<Sighting> sightings = sightingDao.getAllSightings();
        
        assertEquals(2, sightings.size());
        assertTrue(sightings.contains(sighting1));
        assertTrue(sightings.contains(sighting2));
    }
    
    @Test
    public void testUpdateSighting() {
        Location location = new Location(); //Create a location
        location.setName("Test Location Name");
        location.setDescription("Test Location Description");
        location.setAddress("Test Location Address");
        location.setLatitude(52.143602);
        location.setLongitude(-116.554823);
        location = locationDao.addLocation(location);
        List<Location> locations = new ArrayList<>();
        locations.add(location);

        Hero heroVillain = new Hero(); //Create hero
        heroVillain.setName("Test Hero Name");
        heroVillain.setDescription("Test Hero Description");        
        heroVillain.setLocations(locations); //add locations list
        heroVillain = heroDao.addHero(heroVillain); //save hero
        
        Sighting sighting = new Sighting(); //Create a sighting
        sighting.setDate(LocalDate.now());
        sighting.setLocationId(location.getId());
        sighting.setHeroId(heroVillain.getId());        
        sighting = sightingDao.addSighting(sighting); 
        
        Sighting sightingFromDao = sightingDao.getSightingById(sighting.getId());
        assertEquals(sighting, sightingFromDao); //Assert sighting created is equal to sighting saved from db
        
        sighting.setDate(LocalDate.now().minusWeeks(1));
        sightingDao.updateSighting(sighting); 
        
        assertNotEquals(sighting, sightingFromDao); //Assert sighting created is not equal to sighting saved from db, because it was updated
        
        sightingFromDao = sightingDao.getSightingById(sighting.getId());        
        assertEquals(sighting, sightingFromDao); //Assert sighting updated is equal to sighting updated saved from db
    }
    
    @Test
    public void testDeleteSightingById() {
        Location location = new Location(); //Create a location
        location.setName("Test Location Name");
        location.setDescription("Test Location Description");
        location.setAddress("Test Location Address");
        location.setLatitude(52.143602);
        location.setLongitude(-116.554823);
        location = locationDao.addLocation(location);
        List<Location> locations = new ArrayList<>();
        locations.add(location);

        Hero heroVillain = new Hero(); //Create hero
        heroVillain.setName("Test Hero Name");
        heroVillain.setDescription("Test Hero Description");        
        heroVillain.setLocations(locations); //add locations list
        heroVillain = heroDao.addHero(heroVillain); //save hero 
        
        Sighting sighting = new Sighting(); //Create a sighting
        sighting.setDate(LocalDate.now());
        sighting.setLocationId(location.getId());
        sighting.setHeroId(heroVillain.getId());        
        sighting = sightingDao.addSighting(sighting);
        
        Hero savedHero = heroDao.getHeroById(heroVillain.getId());        
        assertEquals(savedHero.getLocations().get(0).getId(), location.getId()); //Assert that the location on the savedHero's locations list is the power we created
       
        Sighting savedSighting = sightingDao.getSightingById(sighting.getId());
               
        Sighting sightingFromDao = sightingDao.getSightingById(sighting.getId()); //get the saved sighting from db
        assertEquals(sighting, sightingFromDao);
        
        sightingDao.deleteSightingById(sighting.getId());
        
        sightingFromDao = sightingDao.getSightingById(sighting.getId());
        assertNull(sightingFromDao); //Assert that the sighting was deleted
        
        Hero heroWithoutPower = heroDao.getHeroById(heroVillain.getId());        
        assertEquals(heroWithoutPower.getLocations().size(), 0); //Assert that this hero doesn't have a location on it's locations list
    
    }
}
