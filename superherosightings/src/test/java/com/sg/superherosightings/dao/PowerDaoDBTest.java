/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.superherosightings.dao;

import com.sg.superherosightings.dto.Hero;
import com.sg.superherosightings.dto.Power;
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
public class PowerDaoDBTest {
    
    @Autowired
    PowerDao powerDao;
    
    @Autowired
    HeroDao heroDao;
    
    public PowerDaoDBTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
        
        List<Power> powers = powerDao.getAllPowers();
        for(Power power : powers) {
            powerDao.deletePowerById(power.getId());
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
    public void testAddAndGetPowerower() {                
        Power power = new Power();
        power.setName("Test Name");
        power.setDescription("Test Description");
        power = powerDao.addPower(power);
        
        Power powerFromDao = powerDao.getPowerById(power.getId());
        
        assertEquals(power, powerFromDao);
    }
    
    @Test
    public void testGetAllPowers() {
        Power power1 = new Power();
        power1.setName("Test Name1");
        power1.setDescription("Test Description1");
        power1 = powerDao.addPower(power1);
        
        Power power2 = new Power();
        power2.setName("Test Name2");
        power2.setDescription("Test Description2");
        power2 = powerDao.addPower(power2);
        
        List<Power> powers = powerDao.getAllPowers();
        
        assertEquals(2, powers.size());
        assertTrue(powers.contains(power1));
        assertTrue(powers.contains(power2));
    }
    
    @Test
    public void testUpdatePower() {
        Power power = new Power();
        power.setName("Test Name");
        power.setDescription("Test Description");
        power = powerDao.addPower(power);
        
        Power powerFromDao = powerDao.getPowerById(power.getId());
        assertEquals(power, powerFromDao);
        
        power.setName("New Name");
        powerDao.updatePower(power);
        
        assertNotEquals(power, powerFromDao);
        
        powerFromDao = powerDao.getPowerById(power.getId());
        
        assertEquals(power, powerFromDao);
    }
    
    @Test
    public void testDeletePowerById() {
        Power power = new Power();
        power.setName("Test Name");
        power.setDescription("Test Description");
        power = powerDao.addPower(power);
        List<Power> powers = new ArrayList<>();
        powers.add(power);

        Hero heroVillain = new Hero(); //Create a Hero
        heroVillain.setName("Test Super Name");
        heroVillain.setDescription("Test Super Description");        
        heroVillain.setPowers(powers); //add powers list to hero
        heroDao.addHero(heroVillain); //save hero
        
        Hero savedHero = heroDao.getHeroById(heroVillain.getId());
        
        assertEquals(savedHero.getPowers().get(0).getId(), power.getId()); //Assert that the power on the savedHero's powers list is the power we created
        
        Power powerFromDao = powerDao.getPowerById(power.getId()); //get the saved power from db
        assertEquals(power, powerFromDao);
        
        powerDao.deletePowerById(power.getId());
        
        powerFromDao = powerDao.getPowerById(power.getId());
        assertNull(powerFromDao); //Assert that the power was deleted
        
        Hero heroWithoutPower = heroDao.getHeroById(heroVillain.getId());
        
        assertEquals(heroWithoutPower.getPowers().size(), 0); //Assert that this hero doesn't have a power on it's powers list
    }
    
}
