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
import java.util.List;

/**
 *
 * @author steve
 */
public interface HeroDao {
    
    Hero getHeroById(int id);
    List<Hero> getAllHeroes();
    Hero addHero(Hero heroVillain);
    void updateHero(Hero heroVillain);
    void deleteHeroById(int id);
    
    List<Hero> getHeroesByLocation(Location location);
    List<Hero> getHeroesByOrganization(Organization organization);
    
    List<Organization> getOrganizationsForHero(int id);
    List<Location> getLocationsForHero(int id);
    List<Power> getPowersForHero(int id);
    
    void removePowerForHero(int heroId, int powerId);
    void removeOrganizationForHero(int heroId, int organizationId);
}
