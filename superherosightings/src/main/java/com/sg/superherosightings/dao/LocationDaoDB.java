/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.superherosightings.dao;

import com.sg.superherosightings.dto.Location;
import java.sql.ResultSet;
import java.sql.SQLException;
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
public class LocationDaoDB implements LocationDao {
    
    @Autowired
    JdbcTemplate jdbc;
    
    final String SELECT_LOCATION_BY_ID = "SELECT * FROM location WHERE id = ?";
    final String SELECT_ALL_LOCATIONS = "SELECT * FROM location";
    final String INSERT_LOCATION = "INSERT INTO location(name, description, address, latitude, longitude) "
                + "VALUES(?,?,?,?,?)";
    final String UPDATE_LOCATION = "UPDATE location SET name = ?, description = ?, "
                + "address = ?, latitude = ?, longitude = ? WHERE id = ?";
    final String DELETE_SIGHTING = "DELETE FROM sightings WHERE location_id = ?";
    final String DELETE_LOCATION = "DELETE FROM location WHERE id = ?";
    
    //Create the SELECT query string and use it in queryForObject to get the one Location we are searching for.
    //Surround the code with a try-catch that will catch the exception thrown when there is no Location with that ID, so we can return null in that situation.
    @Override
    public Location getLocationById(int id) {
        try {
            return jdbc.queryForObject(SELECT_LOCATION_BY_ID, new LocationMapper(), id);
        } catch (DataAccessException ex) {
            return null;
        }
    }
    //The program simply create our SELECT query and use it in the query method to return a list of all Locations.
    //If no Locations are found, it will return an empty list.
    @Override
    public List<Location> getAllLocations() {
        
        return jdbc.query(SELECT_ALL_LOCATIONS, new LocationMapper());
    }
    
    //This method is @Transactional so the program can retrieve the new ID.
    //The program print our INSERT query and use it to insert the basic Location information into the db.
    //The program get a new ID and add it to the Location object.
    //Once the program done that, it can return the Location from the method.
    @Override
    @Transactional
    public Location addLocation(Location location) {
        
        jdbc.update(INSERT_LOCATION,
                location.getName(),
                location.getDescription(),
                location.getAddress(),
                location.getLatitude(),
                location.getLongitude());
        
        int newId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        location.setId(newId);
        return location;
    }
    
    //Use of @Transactional here because the system are making multiple db modifying queries in the method.
    //Print the UPDATE query and use it in the update method with the appropriate data.
    //The program need to handle the Location by first deleting all the sighting entries and then adding them back in with the call to insertSighting.
    @Override
    @Transactional
    public void updateLocation(Location location) {
        
        jdbc.update(UPDATE_LOCATION, 
                location.getName(), 
                location.getDescription(),
                location.getAddress(),
                location.getLatitude(),
                location.getLongitude(),
                location.getId());
    }
    
    //This is @Transactional because of the multiple db modifying queries.
    //First, the program get rid of the sighting entries that reference our Location.
    //Then the program can delete the location.
    @Override
    @Transactional
    public void deleteLocationById(int id) {
        
        jdbc.update(DELETE_SIGHTING, id);
        
        
        jdbc.update(DELETE_LOCATION, id);
    }
    
    public static final class LocationMapper implements RowMapper<Location> {

        @Override
        public Location mapRow(ResultSet rs, int index) throws SQLException {
            Location location = new Location();
            location.setId(rs.getInt("id"));
            location.setName(rs.getString("name"));
            location.setDescription(rs.getString("description"));
            location.setAddress(rs.getString("address"));
            location.setLatitude(rs.getDouble("latitude"));
            location.setLongitude(rs.getDouble("longitude"));
            return location;
        }
    }
}
