SET MODE PostgreSQL;

CREATE TABLE IF NOT EXISTS sections (
  id int PRIMARY KEY auto_increment,
  sectionName VARCHAR,
  completed BOOLEAN,
  categoryid INTEGER
);

CREATE TABLE IF NOT EXISTS categories (
  id int PRIMARY KEY auto_increment,
  name VARCHAR
);

CREATE TABLE IF NOT EXISTS employees (
  id int PRIMARY KEY auto_increment,
  employeeName VARCHAR,
  taskId INTEGER,
  ekNo VARCHAR,
  designation VARCHAR,
  completed BOOLEAN
);