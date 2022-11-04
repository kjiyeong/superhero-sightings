DROP DATABASE IF EXISTS superheroSightingsTestDB;

CREATE DATABASE superheroSightingsTestDB;

USE superheroSightingsTestDB;

CREATE TABLE `superPower` (
id INT PRIMARY KEY AUTO_INCREMENT,
`name` VARCHAR(30) NOT NULL,
`description` VARCHAR(255)
);

CREATE TABLE `superHero` (
id INT PRIMARY KEY AUTO_INCREMENT,
`name` VARCHAR(30) NOT NULL,
`description` VARCHAR(255),
`hero_image` BLOB
);

CREATE TABLE `organization` (
id INT PRIMARY KEY AUTO_INCREMENT,
`name` VARCHAR(40) NOT NULL,
`description` VARCHAR(255),
`contact` VARCHAR(50) NOT NULL,
`address` VARCHAR(150)
);

CREATE TABLE location (
id INT PRIMARY KEY AUTO_INCREMENT,
`name` VARCHAR(50),
`description` VARCHAR(255),
`address` VARCHAR(150),
`latitude` DECIMAL(10, 8),
`longitude` DECIMAL(11, 8)
);

CREATE TABLE hero_organization (
superHero_id INT NOT NULL,
organization_id INT NOT NULL,
PRIMARY KEY(superHero_id, organization_id),
FOREIGN KEY(superHero_id) REFERENCES `superHero`(id),
FOREIGN KEY(organization_id) REFERENCES `organization`(id)
);

CREATE TABLE sightings (
id INT PRIMARY KEY AUTO_INCREMENT,
superHero_id INT NOT NULL,
location_id INT NOT NULL,
`date` DATE NOT NULL,
FOREIGN KEY (superHero_id) REFERENCES `superHero`(id),
FOREIGN KEY(location_id) REFERENCES location(id)
);

CREATE TABLE super_power(
superHero_id INT NOT NULL,
power_id INT NOT NULL,
PRIMARY KEY (superHero_id, power_id),
FOREIGN KEY (superHero_id) REFERENCES `superHero`(id),
FOREIGN KEY (power_id) REFERENCES `superPower`(id)
);