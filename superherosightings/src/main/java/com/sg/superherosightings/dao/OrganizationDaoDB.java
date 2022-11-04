/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.superherosightings.dao;

import com.sg.superherosightings.dto.Organization;
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
public class OrganizationDaoDB implements OrganizationDao {
    
    @Autowired
    JdbcTemplate jdbc;
    
    final String SELECT_ORGANIZATION_BY_ID = "SELECT * FROM organization WHERE id = ?";
    final String SELECT_ALL_ORGANIZATIONS = "SELECT * FROM organization";
    final String INSERT_ORGANIZATION = "INSERT INTO organization(name, description, address, contact) VALUES(?,?,?,?)";
    final String UPDATE_ORGANIZATION = "UPDATE organization SET name = ?, description = ?, "
                + "address = ?, contact = ? WHERE id = ?";
    final String DELETE_HERO_ORGANIZATION = "DELETE FROM hero_organization WHERE organization_id = ?";
    final String DELETE_ORGANIZATION = "DELETE FROM organization WHERE id = ?";

    //Create the SELECT query string and use it in queryForObject to get the one Organization we are searching for.
    //Surround the code with a try-catch that will catch the exception thrown when there is no Organization with that ID, so the system can return null.
    
    @Override
    public Organization getOrganizationById(int id) {
        try {
            return jdbc.queryForObject(SELECT_ORGANIZATION_BY_ID, new OrganizationDaoDB.OrganizationMapper(), id);
        } catch (DataAccessException ex) {
            return null;
        }
    }

    //Create our SELECT query and use it in the query method to return a list of all Organizations.
    //If no Organizations are found, it will return an empty list.
    @Override
    public List<Organization> getAllOrganizations() {
        
        return jdbc.query(SELECT_ALL_ORGANIZATIONS, new OrganizationDaoDB.OrganizationMapper());
    }

    //This method is @Transactional so the system can retrieve the new ID.
    //Print our INSERT query and use it to insert the basic Organization information into the database.
    //The Program get a new ID and add it to the Organization object.
    //The program can then call our insertHeroOrganization helper method, which loops through the list of Heroes in the Organization and adds database entries to sighting for each.
    //Once the system done that, it can return the Organization from the method.
    @Override
    @Transactional
    public Organization addOrganization(Organization organization) {
        
        jdbc.update(INSERT_ORGANIZATION,
                organization.getName(),
                organization.getDescription(),
                organization.getAddress(),
                organization.getContact());
        
        int newId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        organization.setId(newId);
        return organization;
    }

    //Use of @Transactional here because the system making multiple db modifying queries in the method.
    //Print the UPDATE query and use it in the update method with the appropriate data.
    //The program need to handle the Organization by first deleting all the hero_organization entries and then adding them back in with the call to insertHeroOrganization.
    @Override
    @Transactional
    public void updateOrganization(Organization organization) {
        
        jdbc.update(UPDATE_ORGANIZATION, 
                organization.getName(), 
                organization.getDescription(),
                organization.getAddress(),
                organization.getContact(),
                organization.getId());
    }

    @Override
    @Transactional
    public void deleteOrganizationById(int id) {
        
        jdbc.update(DELETE_HERO_ORGANIZATION, id);
        
        jdbc.update(DELETE_ORGANIZATION, id);
    }
    
    public static final class OrganizationMapper implements RowMapper<Organization> {

        @Override
        public Organization mapRow(ResultSet rs, int index) throws SQLException {
            Organization organization = new Organization();
            organization.setId(rs.getInt("id"));
            organization.setName(rs.getString("name"));
            organization.setDescription(rs.getString("description"));
            organization.setAddress(rs.getString("address"));
            organization.setContact(rs.getString("contact"));
            return organization;
        }
    }
}
