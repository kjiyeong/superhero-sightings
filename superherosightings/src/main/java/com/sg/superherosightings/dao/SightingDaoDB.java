/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.superherosightings.dao;

import com.sg.superherosightings.dto.Sighting;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
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
public class SightingDaoDB implements SightingDao {
    
    @Autowired
    JdbcTemplate jdbc;
    
    final String SELECT_SIGHTING_BY_ID = "SELECT * FROM sightings WHERE id = ?";
    final String SELECT_ALL_SIGHTINGS = "SELECT * FROM sightings";
    final String INSERT_SIGHTING = "INSERT INTO sightings(superHero_id, location_id, date) "
                + "VALUES(?,?,?)";
    final String UPDATE_SIGHTING = "UPDATE sightings SET superHero_id = ?, location_id = ?, date = ? "
                + "WHERE id = ?";
    final String DELETE_SIGHTING = "DELETE FROM sightings WHERE id = ?";
    final String SELECT_SIGHTINGS_BY_DATE = "SELECT * FROM sightings WHERE date = ?";

    //Create the SELECT query string and use it in queryForObject to get the one Sighting we are searching for.
    //Surround the code with a try-catch that will catch the exception thrown when there is no Sighting with that ID, so we can return null.
    @Override
    public Sighting getSightingById(int id) {
        try {
            return jdbc.queryForObject(SELECT_SIGHTING_BY_ID, new SightingDaoDB.SightingMapper(), id);
        } catch (DataAccessException ex) {
            return null;
        }
    }
    
    //The program create our SELECT query and use it in the query method to return a list of all Sightings.
    //If no Sighting are found, it will return an empty list.
    @Override
    public List<Sighting> getAllSightings() {
        
        return jdbc.query(SELECT_ALL_SIGHTINGS, new SightingDaoDB.SightingMapper());
    }

    //This method is @Transactional so the program can retrieve the new ID.
    //The program print our INSERT query and use it to insert the sighting information into the db.
    //The program get a new ID and add it to the sighting object.
    //Once the program done that, it can return the sighting from the method.
    @Override
    @Transactional
    public Sighting addSighting(Sighting sighting) {
        
        jdbc.update(INSERT_SIGHTING,
                sighting.getHeroId(),
                sighting.getLocationId(),
                sighting.getDate());
        
        int newId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        sighting.setId(newId);
        return sighting;
    }

    //Create the UPDATE query and use it in the update method with the appropriate sighting data.
    @Override
    public void updateSighting(Sighting sighting) {
        
        jdbc.update(UPDATE_SIGHTING, 
                sighting.getHeroId(), 
                sighting.getLocationId(),
                sighting.getDate(),
                sighting.getId());
    }

    @Override
    public void deleteSightingById(int id) {
        
        jdbc.update(DELETE_SIGHTING, id);
    }

    @Override
    public List<Sighting> getSightingsByDate(LocalDate date) {
        
        return jdbc.query(SELECT_SIGHTINGS_BY_DATE, new SightingDaoDB.SightingMapper());
    }
    
    public static final class SightingMapper implements RowMapper<Sighting> {

        @Override
        public Sighting mapRow(ResultSet rs, int index) throws SQLException {
            Sighting sighting = new Sighting();
            sighting.setId(rs.getInt("id"));
            sighting.setHeroId(rs.getInt("superHero_id"));
            sighting.setLocationId(rs.getInt("location_id"));
            sighting.setDate(rs.getDate("date").toLocalDate());
            return sighting;
        }
    }
    
}
