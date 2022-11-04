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
public class LocationDaoDBTest {
    
    @Autowired
    LocationDao locationDao;
    
    @Autowired
    SightingDao sightingDao;
            
    @Autowired
    HeroDao heroDao;
    
    public LocationDaoDBTest() {
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
    public void testAddAndGetLocation() {        
        Location location = new Location();
        location.setName("Test Name");
        location.setDescription("Test Description");
        location.setAddress("Test Address");
        location.setLatitude(52.143602);
        location.setLongitude(-116.554823);
        location = locationDao.addLocation(location);
        
        Location fromDao = locationDao.getLocationById(location.getId());
        
        assertEquals(location, fromDao);
    }
   
    @Test
    public void testGetAllLocations() {
        Location location1 = new Location();
        location1.setName("Test Location Name1");
        location1.setDescription("Test Location Description1");
        location1.setAddress("Test Location Address1");
        location1.setLatitude(52.143602);
        location1.setLongitude(-116.554823);
        location1 = locationDao.addLocation(location1);
        
        Location location2 = new Location();
        location2.setName("Test Location Name2");
        location2.setDescription("Test Location Description2");
        location2.setAddress("Test Location Address2");
        location2.setLatitude(52.143602);
        location2.setLongitude(-116.554823);
        location2 = locationDao.addLocation(location2);
        
        List<Location> locations = locationDao.getAllLocations();
        
        assertEquals(2, locations.size());
        assertTrue(locations.contains(location1));
        assertTrue(locations.contains(location2));
    }
    
    @Test
    public void testUpdateLocation() {
        Location location = new Location();
        location.setName("Test Location Name");
        location.setDescription("Test Location Description");
        location.setAddress("Test Location Address");
        location.setLatitude(52.143602);
        location.setLongitude(-116.554823);
        location = locationDao.addLocation(location);
        
        Location fromDao = locationDao.getLocationById(location.getId());
        assertEquals(location, fromDao);
        
        location.setName("New Location Name");
        locationDao.updateLocation(location);
        
        assertNotEquals(location, fromDao);
        
        fromDao = locationDao.getLocationById(location.getId());
        
        assertEquals(location, fromDao);
    }

    @Test
    public void testDeleteLocationById() {  
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
        heroVillain = heroDao.addHero(heroVillain); //save Hero
        
        Sighting sighting = new Sighting(); //Create a sighting (link between hero and location)
        sighting.setDate(LocalDate.now());
        sighting.setLocationId(location.getId());
        sighting.setHeroId(heroVillain.getId());        
        sighting = sightingDao.addSighting(sighting);        
        
        Location fromDao = locationDao.getLocationById(location.getId()); //get the saved location from db
        assertEquals(location, fromDao);
        
        Sighting sightingFromDao = sightingDao.getSightingById(sighting.getId()); //get the saved sighting from db
        assertEquals(sighting, sightingFromDao);
        
        locationDao.deleteLocationById(location.getId());
        
        fromDao = locationDao.getLocationById(location.getId());
        assertNull(fromDao);
        assertNull(sightingDao.getSightingById(sighting.getId()));
    }
}
