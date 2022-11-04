/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.superherosightings.dao;

import com.sg.superherosightings.dao.LocationDaoDB.LocationMapper;
import com.sg.superherosightings.dao.OrganizationDaoDB.OrganizationMapper;
import com.sg.superherosightings.dao.PowerDaoDB.PowerMapper;
import com.sg.superherosightings.dto.Hero;
import com.sg.superherosightings.dto.Location;
import com.sg.superherosightings.dto.Organization;
import com.sg.superherosightings.dto.Power;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author steve
 */
@Repository
public class HeroDaoDB implements HeroDao{
    
    @Autowired
    JdbcTemplate jdbc;
    
    final String SELECT_SUPERHERO_BY_ID = "SELECT * FROM superHero WHERE id = ?";
    final String SELECT_ALL_HEROES = "SELECT * FROM superHero";
    final String INSERT_HERO = "INSERT INTO superHero(name, description, hero_image) " // adding image
                + "VALUES(?,?,?)";
    final String INSERT_SUPERPOWER = "INSERT INTO "
                + "super_power(superHero_id, power_id) VALUES(?,?)";
    final String INSERT_ORGANIZATION = "INSERT INTO "
                + "hero_organization(superHero_id, organization_id) VALUES(?,?)";
    final String UPDATE_HERO = "UPDATE superHero SET name = ?, description = ? WHERE id = ?";
    final String DELETE_SUPER_POWER = "DELETE FROM super_power WHERE superHero_id = ?";
    final String DELETE_HERO_ORGANIZATION = "DELETE FROM hero_organization WHERE superHero_id = ?";
    final String DELETE_SIGHTINGS = "DELETE FROM sightings WHERE superHero_id = ?";
    final String DELETE_SUPER = "DELETE FROM superHero WHERE id = ?";
    final String SELECT_HEROES_FOR_LOCATION = "SELECT s.* FROM superHero s JOIN "
                + "sightings sig ON sig.superHero_id = s.id WHERE sig.location_id = ?";
    final String SELECT_HEROES_FOR_ORGANIZATION = "SELECT s.* FROM superHero s JOIN "
                + "hero_organization ho ON ho.superHero_id = s.id WHERE ho.organization_id = ?";
    final String SELECT_ORGANIZATIONS_FOR_HERO = "SELECT o.* FROM organization o "
                + "JOIN hero_organization ho ON ho.organization_id = o.id WHERE ho.superHero_id = ?";
    final String SELECT_LOCATIONS_FOR_HERO = "SELECT l.* FROM location l "
                + "JOIN sightings si ON si.location_id = l.id WHERE si.superHero_id = ?";
    final String SELECT_POWERS_FOR_HERO = "SELECT p.* FROM superPower p "
                + "JOIN super_power sp ON sp.power_id = p.id WHERE sp.superHero_id = ?";
    final String DELETE_POWER_FOR_HERO = "DELETE FROM super_power WHERE superHero_id = ? AND power_id = ?";
    final String DELETE_ORGANIZATION_FOR_HERO = "DELETE FROM hero_organization WHERE superHero_id = ? AND organization_id = ?";
    
    //Start with a SELECT query to get the basic Hero object.
    //Then call to this getLocationsForHero method, where JOIN from Location to sighting to get a list of Location objects for this Hero.
    //Then call the getPowersForHero method, where JOIN from Power to super_power to get a list of Power objects for this Hero.
    //Then call the getOrganizationsForHero method, where JOIN from Location to hero_organization to get a list of Organization objects for this Hero.
    //If the hero catch an exception in getHerorById, it will return null because it means the Hero does not exist.
    
    @Override
    public Hero getHeroById(int id) {
        try {
            
            Hero heroVillain = jdbc.queryForObject(SELECT_SUPERHERO_BY_ID, new HeroMapper(), id);
            heroVillain.setLocations(getLocationsForHero(id)); //get rid of location.  Does not belong to the hero.
            heroVillain.setPowers(getPowersForHero(id)); //set list of powers
            heroVillain.setOrganizations(getOrganizationsForHero(id)); //set list of organizations
            return heroVillain;
        } catch(DataAccessException ex) {
            return null;
        }
    }
    
    //Start by writing a SELECT query and using it to get the list of Heroes.
    //Then pass the list of Heroes into associatePowerOrganizationSighting, where we loop through the list and call our existing Location, 
    //Power and Organization methods to fill in the data for each Hero.
    @Override
    public List<Hero> getAllHeroes() {
        
        List<Hero> heroes = jdbc.query(SELECT_ALL_HEROES, new HeroMapper());
        associatePowerOrganizationSighting(heroes);
        return heroes;
    }
    
    private void associatePowerOrganizationSighting(List<Hero> heroes ) {
        for (Hero heroVillain : heroes) {
            heroVillain.setPowers(getPowersForHero(heroVillain.getId()));
            heroVillain.setOrganizations(getOrganizationsForHero(heroVillain.getId()));
            heroVillain.setLocations(getLocationsForHero(heroVillain.getId()));
        }
    }
    
    //Just like the previous add method, this is @Transactional so we can retrieve the new ID.
    //Print our INSERT query and use it to insert the Hero information into the DB.
    //The program get a new ID and add it to the Hero object.
    //The program can then call our insertPower helper method, which loops through the list of Powers in the Hero and adds DB entries to super_power for each.
    //The program can then call our insertOrganization helper method, which loops through the list of Organizations in the Hero and adds DB entries to hero_organization for each.
    //Once the program done that, it can return the Course from the method.
    @Override
    @Transactional
    public Hero addHero(Hero heroVillain) {
        
        
        jdbc.update(INSERT_HERO,
                heroVillain.getName(),
                heroVillain.getDescription(),
                heroVillain.getHeroImage()  // adding image
        );

        int newId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        heroVillain.setId(newId);

        if(heroVillain.getPowers() != null && !heroVillain.getPowers().isEmpty()) {        
            insertPower(heroVillain);
        }
        if(heroVillain.getOrganizations() != null && !heroVillain.getOrganizations().isEmpty()) {
            insertOrganization(heroVillain);
        }
        return heroVillain;
    }
    //Loops through the list of Powers in the Hero and adds DB entries to super_power for each.
    private void insertPower(Hero heroVillain) {
        
        for(Power power : heroVillain.getPowers()) {
            jdbc.update(INSERT_SUPERPOWER, 
                    heroVillain.getId(),
                    power.getId());
        }
    }
    //Loops through the list of Organizations in the Hero and adds DB entries to hero_organization for each.
    private void insertOrganization(Hero heroVillain) {
        
        for(Organization organization : heroVillain.getOrganizations()) {
            jdbc.update(INSERT_ORGANIZATION, 
                    heroVillain.getId(),
                    organization.getId());
        }
    }
    
    //Using @Transactional here because making multiple DB modifying queries in the method.
    //Print the UPDATE query and use it in the update method with the appropriate data.
    //The program need to handle the Powers by first deleting all the super_power entries and then adding them back in with the call to insertPower.
    //The program handle the Organizations by first deleting all the hero_organization entries and then adding them back in with the call to insertOrganization.
    
    @Override
    @Transactional
    public void updateHero(Hero heroVillain) {
        
        jdbc.update(UPDATE_HERO, 
                heroVillain.getName(), 
                heroVillain.getDescription(), 
                heroVillain.getId());
        
        
        jdbc.update(DELETE_SUPER_POWER, heroVillain.getId());
        insertPower(heroVillain);
        
        
        jdbc.update(DELETE_HERO_ORGANIZATION, heroVillain.getId());        
        insertOrganization(heroVillain);
    }
    
    //This is @Transactional because of the multiple DB modifying queries.
    //First, the program get rid of the sighting entries that reference our Heror.
    //Second, the program rid of the super_power entries that reference our Hero.
    //Third, the program rid of the super_organization entries that reference our Hero.
    //Then the program can delete the Hero.
    @Override
    @Transactional
    public void deleteHeroById(int id) {
        
        jdbc.update(DELETE_SIGHTINGS, id);
        
        
        jdbc.update(DELETE_SUPER_POWER, id);
        
        
        jdbc.update(DELETE_HERO_ORGANIZATION, id);
        
        
        jdbc.update(DELETE_SUPER, id);
    }
    
    //JOIN with hero_location so the program can limit the query based on the location_id. 
    //Once the program have the list, the program use associatePowerOrganizationSighting to fill in the rest of the data.
    @Override
    public List<Hero> getHeroesByLocation(Location location) {
        
        List<Hero> heroes = jdbc.query(SELECT_HEROES_FOR_LOCATION, 
                new HeroMapper(), location.getId());
        
        Set<Hero> set = new HashSet<>(heroes); //remove duplicate heroes from list
        heroes.clear();
        heroes.addAll(set);

        associatePowerOrganizationSighting(heroes);
        return heroes;
    }
    
    //JOIN with hero_location so the program can limit the query based on the organization_id. 
    //Once we have the list, the program use associatePowerOrganizationSighting to fill in the rest of the data.
    @Override
    public List<Hero> getHeroesByOrganization(Organization organization) {
        
        List<Hero> heroes = jdbc.query(SELECT_HEROES_FOR_ORGANIZATION, 
                new HeroMapper(), organization.getId());
        associatePowerOrganizationSighting(heroes);
        return heroes;
    }
    
    //JOIN from Location to hero_organization to get a list of Organization objects for this Hero.
    @Override
    public List<Organization> getOrganizationsForHero(int id) {
        
        return jdbc.query(SELECT_ORGANIZATIONS_FOR_HERO, new OrganizationMapper(), id); 
    }
    
    //JOIN from Location to sighting to get a list of Location objects for this Hero.
    @Override
    public List<Location> getLocationsForHero(int id) {
        
        List<Location> locations = jdbc.query(SELECT_LOCATIONS_FOR_HERO, new LocationMapper(), id);
        
        Set<Location> set = new HashSet<>(locations); //remove duplicate locations from list
        locations.clear();
        locations.addAll(set);

        return locations;
    }
    
    //JOIN from Power to super_power to get a list of Power objects for this Hero
    @Override
    public List<Power> getPowersForHero(int id) {
        
        return jdbc.query(SELECT_POWERS_FOR_HERO, new PowerMapper(), id); 
    }

    @Override
    public void removePowerForHero(int heroId, int powerId) {
        
        jdbc.update(DELETE_POWER_FOR_HERO, heroId, powerId);
    }

    @Override
    public void removeOrganizationForHero(int heroId, int organizationId) {
        
        jdbc.update(DELETE_ORGANIZATION_FOR_HERO, heroId, organizationId);
    }
    
    public static final class HeroMapper implements RowMapper<Hero> {

        @Override
        public Hero mapRow(ResultSet rs, int index) throws SQLException {
            Hero heroVillain = new Hero();
            heroVillain.setId(rs.getInt("id"));
            heroVillain.setName(rs.getString("name"));
            heroVillain.setDescription(rs.getString("description"));
            heroVillain.setHeroImage(rs.getBytes("hero_image")); // adding image file
        
            return heroVillain;
        }
    }
    
}
