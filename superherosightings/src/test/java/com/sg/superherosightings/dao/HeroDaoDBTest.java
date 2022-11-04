/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.superherosightings.dao;

import com.sg.superherosightings.dto.Hero;
import com.sg.superherosightings.dto.Location;
import com.sg.superherosightings.dto.Organization;
import com.sg.superherosightings.dto.Power;
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
public class HeroDaoDBTest {
    
    @Autowired
    LocationDao locationDao;
    
    @Autowired
    OrganizationDao organizationDao;
    
    @Autowired
    PowerDao powerDao;
    
    @Autowired
    SightingDao sightingDao;
            
    @Autowired
    HeroDao heroDao;
    
    public HeroDaoDBTest() {
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
        
        List<Organization> organizations = organizationDao.getAllOrganizations();
        for(Organization organization : organizations) {
            organizationDao.deleteOrganizationById(organization.getId());
        }
        
        List<Power> powers = powerDao.getAllPowers();
        for(Power power : powers) {
            powerDao.deletePowerById(power.getId());
        }
        
        List<Sighting> sightings = sightingDao.getAllSightings();
        for(Sighting sighting : sightings) {
            sightingDao.deleteSightingById(sighting.getId());
        }
        
        List<Hero> heroes = heroDao.getAllHeroes();
        for(Hero hero_villain : heroes) {
            heroDao.deleteHeroById(hero_villain.getId());
        }
    }
    
    @AfterEach
    public void tearDown() {
    }

     @Test
    public void testAddAndGetHeroById() {
        Organization organization = new Organization(); //Create Organization
        organization.setName("Test Name");
        organization.setDescription("Test Description");
        organization.setAddress("Test Address");
        organization.setContact("Test Contact");
        organization = organizationDao.addOrganization(organization); //save organization
        List<Organization> organizations = new ArrayList<>();
        organizations.add(organization);
        
        Power power = new Power(); //Create Power
        power.setName("Test Name");
        power.setDescription("Test Description");
        power = powerDao.addPower(power); //save power
        List<Power> powers = new ArrayList<>();
        powers.add(power);
        
        Location location = new Location(); //Create a location
        location.setName("Test Location Name");
        location.setDescription("Test Location Description");
        location.setAddress("Test Location Address");
        location.setLatitude(52.143602);
        location.setLongitude(-116.554823);
        location = locationDao.addLocation(location); //save location
        List<Location> locations = new ArrayList<>();
        locations.add(location);

        Hero heroVillain = new Hero(); //Create hero
        heroVillain.setName("Test Hero Name");
        heroVillain.setDescription("Test Hero Description");        
        heroVillain.setLocations(locations); //add locations list
        heroVillain.setOrganizations(organizations); //add organizations list
        heroVillain.setPowers(powers); //add powers list
        heroVillain = heroDao.addHero(heroVillain); //save hero
        
        Sighting sighting = new Sighting(); //Create a sighting
        sighting.setDate(LocalDate.now());
        sighting.setLocationId(location.getId());
        sighting.setHeroId(heroVillain.getId());        
        sightingDao.addSighting(sighting); //save sighting
        
        Hero heroVillainFromDao = heroDao.getHeroById(heroVillain.getId()); //get the saved super from db
        assertEquals(heroVillain, heroVillainFromDao); 
    }
    
    @Test
    public void testGetLocationsForHero() {          
        Location location = new Location(); //Create location1
        location.setName("Test Location Name");
        location.setDescription("Test Location Description");
        location.setAddress("Test Location Address");
        location.setLatitude(52.143602);
        location.setLongitude(-116.554823);
        location = locationDao.addLocation(location); //save location
        
        Location location2 = new Location(); //Create location2
        location2.setName("Test Location Name2");
        location2.setDescription("Test Location Description2");
        location2.setAddress("Test Location Address2");
        location2.setLatitude(52.143602);
        location2.setLongitude(-116.554823);
        location2 = locationDao.addLocation(location2); //save location
        
        List<Location> locations = new ArrayList<>();
        locations.add(location);
        locations.add(location2);

        Hero heroVillain = new Hero(); //Create hero
        heroVillain.setName("Test Hero Name");
        heroVillain.setDescription("Test Hero Description");        
        heroVillain.setLocations(locations); //add locations list
        heroVillain = heroDao.addHero(heroVillain); //save hero
        
        Sighting sighting = new Sighting(); //Create sighting
        sighting.setDate(LocalDate.now());
        sighting.setLocationId(location.getId());
        sighting.setHeroId(heroVillain.getId());        
        sightingDao.addSighting(sighting); //save sighting
        
        Sighting sighting2 = new Sighting(); //Create sighting2
        sighting2.setDate(LocalDate.now());
        sighting2.setLocationId(location2.getId());
        sighting2.setHeroId(heroVillain.getId());        
        sightingDao.addSighting(sighting2); //save sighting2
        
        List<Location> locationsForHeroFromDao = heroDao.getLocationsForHero(heroVillain.getId()); //get the locations for super from db
                
        assertEquals(2, locationsForHeroFromDao.size());
        assertTrue(locationsForHeroFromDao.contains(location));
        assertTrue(locationsForHeroFromDao.contains(location2));
    }
    
    @Test
    public void testGetPowersForHero() {
        Power power = new Power(); //Create Power
        power.setName("Test Name");
        power.setDescription("Test Description");
        power = powerDao.addPower(power); //save power
        
        Power power2 = new Power(); //Create Power2
        power2.setName("Test Name");
        power2.setDescription("Test Description");
        power2 = powerDao.addPower(power2); //save power2
        
        List<Power> powers = new ArrayList<>();
        powers.add(power);
        powers.add(power2);

        Hero heroVillain = new Hero(); //Create hero
        heroVillain.setName("Test Hero Name");
        heroVillain.setDescription("Test Hero Description");        
        heroVillain.setPowers(powers); //add powers list
        heroVillain = heroDao.addHero(heroVillain); //save hero
        
        List<Power> powersForHeroFromDao = heroDao.getPowersForHero(heroVillain.getId()); //get the powers for hero from db
                
        assertEquals(2, powersForHeroFromDao.size());
        assertTrue(powersForHeroFromDao.contains(power));
        assertTrue(powersForHeroFromDao.contains(power2));
    }

    @Test
    public void testGetOrganizationsForHero() {
        Organization organization = new Organization(); //Create Organization
        organization.setName("Test Name");
        organization.setDescription("Test Description");
        organization.setAddress("Test Address");
        organization.setContact("Test Contact");
        organization = organizationDao.addOrganization(organization); //save organization
        
        Organization organization2 = new Organization(); //Create Organization2
        organization2.setName("Test Name2");
        organization2.setDescription("Test Description2");
        organization2.setAddress("Test Address2");
        organization2.setContact("Test Contact2");
        organization2 = organizationDao.addOrganization(organization2); //save organization2
        
        List<Organization> organizations = new ArrayList<>();
        organizations.add(organization);
        organizations.add(organization2);

        Hero heroVillain = new Hero(); //Create hero
        heroVillain.setName("Test Hero Name");
        heroVillain.setDescription("Test Hero Description");        
        heroVillain.setOrganizations(organizations); //add organizations list
        heroVillain = heroDao.addHero(heroVillain); //save hero
        
        List<Organization> organizationsForHeroFromDao = heroDao.getOrganizationsForHero(heroVillain.getId()); //get the organizations for hero from db
        
        assertEquals(2, organizationsForHeroFromDao.size());
        assertTrue(organizationsForHeroFromDao.contains(organization));
        assertTrue(organizationsForHeroFromDao.contains(organization2));
    }
    
    @Test
    public void testGetAllHeroes() {
        Hero heroVillain = new Hero(); //Create hero
        heroVillain.setName("Test Hero Name");
        heroVillain.setDescription("Test Hero Description"); 
        heroVillain.setLocations(new ArrayList());
        heroVillain.setPowers(new ArrayList());
        heroVillain.setOrganizations(new ArrayList());
        heroVillain = heroDao.addHero(heroVillain); //save hero

        
        Hero heroVillain2 = new Hero(); //Create hero2
        heroVillain2.setName("Test Hero2 Name");
        heroVillain2.setDescription("Test Hero2 Description");  
        heroVillain2.setLocations(new ArrayList());
        heroVillain2.setPowers(new ArrayList());
        heroVillain2.setOrganizations(new ArrayList());
        heroVillain2 = heroDao.addHero(heroVillain2); //save hero2
       
        
        List<Hero> heroesFromDao = heroDao.getAllHeroes();
        
        assertEquals(2, heroesFromDao.size());
        assertTrue(heroesFromDao.contains(heroVillain));
        assertTrue(heroesFromDao.contains(heroVillain2));
    }

    @Test
    public void testUpdateHero() {
        Hero heroVillain = new Hero(); //Create hero
        heroVillain.setName("Test Hero Name");
        heroVillain.setDescription("Test Hero Description");   
        heroVillain.setLocations(new ArrayList());
        heroVillain.setPowers(new ArrayList());
        heroVillain.setOrganizations(new ArrayList());
        heroVillain = heroDao.addHero(heroVillain); //save hero
        
        Hero heroFromDao = heroDao.getHeroById(heroVillain.getId());
        assertEquals(heroVillain, heroFromDao);
        
        heroVillain.setName("New Name");
        heroDao.updateHero(heroVillain);
        
        assertNotEquals(heroVillain, heroFromDao);
        
        heroFromDao = heroDao.getHeroById(heroVillain.getId());
        
        assertEquals(heroVillain, heroFromDao);
    }

    @Test
    public void testDeleteHeroById() {
        Organization organization = new Organization(); //Create Organization
        organization.setName("Test Name");
        organization.setDescription("Test Description");
        organization.setAddress("Test Address");
        organization.setContact("Test Contact");
        organization = organizationDao.addOrganization(organization); //save organization
        List<Organization> organizations = new ArrayList<>();
        organizations.add(organization);
        
        Power power = new Power(); //Create Power
        power.setName("Test Name");
        power.setDescription("Test Description");
        power = powerDao.addPower(power); //save power
        List<Power> powers = new ArrayList<>();
        powers.add(power);
        
        Location location = new Location(); //Create a location
        location.setName("Test Location Name");
        location.setDescription("Test Location Description");
        location.setAddress("Test Location Address");
        location.setLatitude(52.143602);
        location.setLongitude(-116.554823);
        location = locationDao.addLocation(location); //save location
        List<Location> locations = new ArrayList<>();
        locations.add(location);

        Hero heroVillain = new Hero(); //Create hero
        heroVillain.setName("Test Hero Name");
        heroVillain.setDescription("Test Hero Description");        
        heroVillain.setLocations(locations); //add locations list
        heroVillain.setOrganizations(organizations); //add organizations list
        heroVillain.setPowers(powers); //add powers list
        heroVillain = heroDao.addHero(heroVillain); //save Hero
        
        Sighting sighting = new Sighting(); //Create a sighting
        sighting.setDate(LocalDate.now());
        sighting.setLocationId(location.getId());
        sighting.setHeroId(heroVillain.getId());        
        sightingDao.addSighting(sighting); //save sighting               
        
        Hero savedHero = heroDao.getHeroById(heroVillain.getId()); //get the saved hero from db
        assertEquals(heroVillain, savedHero);
        
        heroDao.deleteHeroById(heroVillain.getId());
        
        savedHero = heroDao.getHeroById(heroVillain.getId());
        assertNull(savedHero); //Assert that the power was deleted 
    }
    
     @Test
    public void testGetHerosByLocation() {
        Location location = new Location(); //Create location1
        location.setName("Test Location Name");
        location.setDescription("Test Location Description");
        location.setAddress("Test Location Address");
        location.setLatitude(52.143602);
        location.setLongitude(-116.554823);
        location = locationDao.addLocation(location); //save location
        
        List<Location> locations = new ArrayList<>();
        locations.add(location);

        Hero heroVillain = new Hero(); //Create hero
        heroVillain.setName("Test Hero Name");
        heroVillain.setDescription("Test Hero Description");        
        heroVillain.setLocations(locations); //add locations list
        heroVillain.setPowers(new ArrayList());
        heroVillain.setOrganizations(new ArrayList());
        heroVillain = heroDao.addHero(heroVillain); //save hero
        
        Hero heroVillain2 = new Hero(); //Create hero2
        heroVillain2.setName("Test Hero2 Name");
        heroVillain2.setDescription("Test Hero2 Description");        
        heroVillain2.setLocations(locations); //add locations list
        heroVillain2.setPowers(new ArrayList());
        heroVillain2.setOrganizations(new ArrayList());
        heroVillain2 = heroDao.addHero(heroVillain2); //save hero2
        
        Sighting sighting = new Sighting(); //Create sighting
        sighting.setDate(LocalDate.now());
        sighting.setLocationId(location.getId());
        sighting.setHeroId(heroVillain.getId());        
        sightingDao.addSighting(sighting); //save sighting
        
        Sighting sighting2 = new Sighting(); //Create sighting2
        sighting2.setDate(LocalDate.now());
        sighting2.setLocationId(location.getId());
        sighting2.setHeroId(heroVillain2.getId());        
        sightingDao.addSighting(sighting2); //save sighting2
        
        List<Hero> heroesByLocationFromDao = heroDao.getHeroesByLocation(location); //get the saved heros by location from db
        
        assertEquals(2, heroesByLocationFromDao.size());
        assertTrue(heroesByLocationFromDao.contains(heroVillain));
        assertTrue(heroesByLocationFromDao.contains(heroVillain2));
    }

    @Test
    public void testGetHeroesByOrganization() {
        Organization organization = new Organization(); //Create Organization
        organization.setName("Test Name");
        organization.setDescription("Test Description");
        organization.setAddress("Test Address");
        organization.setContact("Test Contact");
        organization = organizationDao.addOrganization(organization); //save organization
        List<Organization> organizations = new ArrayList<>();
        organizations.add(organization);

        Hero heroVillain = new Hero(); //Create hero
        heroVillain.setName("Test Hero Name");
        heroVillain.setDescription("Test Hero Description");        
        heroVillain.setLocations(new ArrayList()); //set empty list
        heroVillain.setPowers(new ArrayList()); //set empty list
        heroVillain.setOrganizations(organizations); //add organizations list
        heroVillain = heroDao.addHero(heroVillain); //save hero
        
        Hero heroVillain2 = new Hero(); //Create hero2
        heroVillain2.setName("Test Hero2 Name");
        heroVillain2.setDescription("Test Hero2 Description");        
        heroVillain2.setLocations(new ArrayList()); //set empty list
        heroVillain2.setPowers(new ArrayList()); //set empty list
        heroVillain2.setOrganizations(organizations); //add organizations list
        heroVillain2 = heroDao.addHero(heroVillain2); //save hero2
        
        List<Hero> heroesByOrganizationFromDao = heroDao.getHeroesByOrganization(organization); //get the saved heroes by organization from db
        
        assertEquals(2, heroesByOrganizationFromDao.size());
        assertTrue(heroesByOrganizationFromDao.contains(heroVillain));
        assertTrue(heroesByOrganizationFromDao.contains(heroVillain2));
    }
    
    @Test
    public void testRemovePowerForSuper() {
        Power power = new Power(); //Create a power
        power.setName("Test Name");
        power.setDescription("Test Description");
        power = powerDao.addPower(power); //save power
        List<Power> powers = new ArrayList<>();
        powers.add(power);

        Hero heroVillain = new Hero(); //Create a Hero
        heroVillain.setName("Test Super Name");
        heroVillain.setDescription("Test Super Description");        
        heroVillain.setPowers(powers); //add powers list to hero
        heroDao.addHero(heroVillain); //save hero
        
        Hero savedHero = heroDao.getHeroById(heroVillain.getId());
        assertEquals(savedHero.getPowers().size(), 1); //There is 1 power in the list
        
        heroDao.removePowerForHero(heroVillain.getId(), power.getId()); //Call function to delete link between hero and power
        
        savedHero = heroDao.getHeroById(heroVillain.getId());
        assertEquals(savedHero.getPowers().size(), 0); //There is no power in the list, the link was deleted
    
        Power savedPower = powerDao.getPowerById(power.getId());
        assertEquals(power, savedPower); //Assert that the power still exists
    }

    @Test
    public void testRemoveOrganizationForHero() {
        Organization organization = new Organization(); //Create organization
        organization.setName("Test Name");
        organization.setDescription("Test Description");
        organization.setAddress("Test Address");
        organization.setContact("Test Contact");
        organization = organizationDao.addOrganization(organization); //save organization
        List<Organization> organizations = new ArrayList<>();
        organizations.add(organization);

        Hero heroVillain = new Hero(); //Create a hero
        heroVillain.setName("Test Hero Name");
        heroVillain.setDescription("Test Hero Description");        
        heroVillain.setOrganizations(organizations); //add organizations list to hero
        heroDao.addHero(heroVillain); //save hero 
        
        Hero savedHero = heroDao.getHeroById(heroVillain.getId());
        assertEquals(savedHero.getOrganizations().size(), 1); //There is 1 organization in the list 
    
        heroDao.removeOrganizationForHero(heroVillain.getId(), organization.getId()); //Call function to delete link between hero and organization
        
        savedHero = heroDao.getHeroById(heroVillain.getId());
        assertEquals(savedHero.getOrganizations().size(), 0); //There is no organization in the list, the link was deleted
    
        Organization savedOrganization = organizationDao.getOrganizationById(organization.getId());
        assertEquals(organization, savedOrganization); //Assert that the organization still exists
    }

}
