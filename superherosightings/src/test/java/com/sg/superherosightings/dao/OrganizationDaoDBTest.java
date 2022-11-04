/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.superherosightings.dao;

import com.sg.superherosightings.dto.Hero;
import com.sg.superherosightings.dto.Organization;
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
public class OrganizationDaoDBTest {
    
    @Autowired
    OrganizationDao organizationDao;    
                
    @Autowired
    HeroDao heroDao;
    
    public OrganizationDaoDBTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
        
        List<Organization> organizations = organizationDao.getAllOrganizations();
        for(Organization organization : organizations) {
            organizationDao.deleteOrganizationById(organization.getId());
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
    public void testAddAndGetOrganization() {
        Organization organization = new Organization();
        organization.setName("Test Name");
        organization.setDescription("Test Description");
        organization.setAddress("Test Address");
        organization.setContact("Test Contact");
        organization = organizationDao.addOrganization(organization);
        
        Organization fromDao = organizationDao.getOrganizationById(organization.getId());
        
        assertEquals(organization, fromDao);
    }
    
    @Test
    public void testGetAllOrganizations() {
        Organization organization1 = new Organization();
        organization1.setName("Test Name");
        organization1.setDescription("Test Description");
        organization1.setAddress("Test Address");
        organization1.setContact("Test Contact");
        organization1 = organizationDao.addOrganization(organization1);
        
        Organization organization2 = new Organization();
        organization2.setName("Test Name2");
        organization2.setDescription("Test Description2");
        organization2.setAddress("Test Address2");
        organization2.setContact("Test Contact2");
        organization2 = organizationDao.addOrganization(organization2);
        
        List<Organization> organizations = organizationDao.getAllOrganizations();
        
        assertEquals(2, organizations.size());
        assertTrue(organizations.contains(organization1));
        assertTrue(organizations.contains(organization2));
    }
    
    @Test
    public void testUpdateOrganization() {
        Organization organization = new Organization();
        organization.setName("Test Name");
        organization.setDescription("Test Description");
        organization.setAddress("Test Address");
        organization.setContact("Test Contact");
        organization = organizationDao.addOrganization(organization);
        
        Organization fromDao = organizationDao.getOrganizationById(organization.getId());
        assertEquals(organization, fromDao);
        
        organization.setName("New Name");
        organizationDao.updateOrganization(organization);
        
        assertNotEquals(organization, fromDao);
        
        fromDao = organizationDao.getOrganizationById(organization.getId());
        
        assertEquals(organization, fromDao);
    }
    
    @Test
    public void testDeleteOrganizationById() {
        Organization organization = new Organization(); //Create organization
        organization.setName("Test Name");
        organization.setDescription("Test Description");
        organization.setAddress("Test Address");
        organization.setContact("Test Contact");
        organization = organizationDao.addOrganization(organization);
        List<Organization> organizations = new ArrayList<>();
        organizations.add(organization);

        Hero heroVillain = new Hero(); //Create a hero
        heroVillain.setName("Test Super Name");
        heroVillain.setDescription("Test Super Description");        
        heroVillain.setOrganizations(organizations); //add organizations list
        heroDao.addHero(heroVillain); //save hero
        
        Hero savedSuper = heroDao.getHeroById(heroVillain.getId());
        
        assertEquals(savedSuper.getOrganizations().get(0).getId(), organization.getId());
        
        Organization organizationFromDao = organizationDao.getOrganizationById(organization.getId()); //get the saved organization from db
        assertEquals(organization, organizationFromDao); //Assert that the organization created is the same as the organization get from the db
        
        organizationDao.deleteOrganizationById(organization.getId());
        
        organizationFromDao = organizationDao.getOrganizationById(organization.getId());
        assertNull(organizationFromDao); //Assert that the organization was deleted
        
        Hero heroWithoutOrganization = heroDao.getHeroById(heroVillain.getId());
        
        assertEquals(heroWithoutOrganization.getOrganizations().size(), 0); //Assert that this hero doesn't have an organization        
    }
    
    
}
