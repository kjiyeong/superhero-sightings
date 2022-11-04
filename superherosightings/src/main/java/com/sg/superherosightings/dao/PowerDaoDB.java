/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.superherosightings.dao;

import com.sg.superherosightings.dto.Power;
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
public class PowerDaoDB implements PowerDao {
    
    @Autowired
    JdbcTemplate jdbc;
    
    final String SELECT_POWER_BY_ID = "SELECT * FROM superPower WHERE id = ?";
    final String SELECT_ALL_POWERS = "SELECT * FROM superPower";
    final String INSERT_POWER = "INSERT INTO superPower(name, description) "
                + "VALUES(?,?)";
    final String UPDATE_POWER = "UPDATE superPower SET name = ?, description = ? "
                + "WHERE id = ?";
    final String DELETE_SUPER_POWER = "DELETE FROM super_power WHERE power_id = ?";
    final String DELETE_POWER = "DELETE FROM superPower WHERE id = ?";
    
    //Create the SELECT query string and use it in queryForObject to get the one Power we are searching for.
    //Surround the code with a try-catch that will catch the exception thrown when there is no Power with that ID, so we can return null.
    @Override
    public Power getPowerById(int id) {
        try {
            return jdbc.queryForObject(SELECT_POWER_BY_ID, new PowerDaoDB.PowerMapper(), id);
        } catch (DataAccessException ex) {
            return null;
        }
    }
    
    //The program create our SELECT query and use it in the query method to return a list of all Powers.
    //If no Powers are found, it will return an empty list.
    @Override
    public List<Power> getAllPowers() {
       
        return jdbc.query(SELECT_ALL_POWERS, new PowerMapper());
    }

    //The method is @Transactional because the program using the LAST_INSERT_ID query later.
    //It create our INSERT query and use it with the update method and the Power data in order.
    //Then get the ID for the new Power using the LAST_INSERT_ID MySQL function and set it in the Power before returning it.    
    @Override
    @Transactional
    public Power addPower(Power power) {
        
        jdbc.update(INSERT_POWER,
                power.getName(),
                power.getDescription());
        
        int newId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        power.setId(newId);
        return power;
    }

    //Create the UPDATE query and use it in the update method with the appropriate Power data.
    @Override
    public void updatePower(Power power) {
        
        jdbc.update(UPDATE_POWER, 
                power.getName(), 
                power.getDescription(),
                power.getId()); 
    }

    //The method @Transactional because the system running multiple queries that modify the db in this method.
    //Start by deleting the super_power entries associated with the Power.
    //Then end by deleting the Power itself.
    //Order matters here because it can't delete something that is being referenced by another table.
    @Override
    @Transactional
    public void deletePowerById(int id) {
        
        jdbc.update(DELETE_SUPER_POWER, id);
        
        jdbc.update(DELETE_POWER, id);
    }
    
    public static final class PowerMapper implements RowMapper<Power> {

        @Override
        public Power mapRow(ResultSet rs, int index) throws SQLException {
            Power power = new Power();
            power.setId(rs.getInt("id"));
            power.setName(rs.getString("name"));
            power.setDescription(rs.getString("description"));
            return power;
        }
    }
    
}
