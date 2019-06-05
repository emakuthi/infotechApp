SET MODE PostgreSQL;

CREATE TABLE IF NOT EXISTS sections (
id int PRIMARY KEY auto_increment,
sectionName VARCHAR,
completed BOOLEAN,
departmentId INTEGER);

CREATE TABLE IF NOT EXISTS departments (
id int PRIMARY KEY auto_increment,
name VARCHAR);


CREATE TABLE IF NOT EXISTS employees (
id int PRIMARY KEY auto_increment,
employeeName VARCHAR,
sectionId INTEGER,
ekNo VARCHAR,
designation VARCHAR,
completed BOOLEAN);