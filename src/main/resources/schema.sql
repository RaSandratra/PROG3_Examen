CREATE TYPE continent_type AS ENUM('AFRICA', 'EUROPA', 'ASIA', 'AMERICA');
CREATE TABLE team (
  id INT NOT NULL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  continent continent_type);

CREATE TYPE position_type AS ENUM('GK','DEF','MIDF','STR');
CREATE TABLE Player (
  id INT NOT NULL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  age INT NOT NULL,
  position position_type NOT NULL,
  id_team INT NOT NULL,
  CONSTRAINT Player_id_FK FOREIGN KEY (id_team) REFERENCES team (id));



  INSERT INTO team (id, name, continent) VALUES
(1, 'Real Madrid CF', 'EUROPA'),
(2, 'FC Barcelona', 'EUROPA'),
(3, 'Atlético de Madrid', 'EUROPA'),
(4, 'Al Ahly SC', 'AFRICA'),
(5, 'Inter Miami CF', 'AMERICA');

INSERT INTO player (id, name, age, position, id_team) VALUES
(1, 'Thibaut Courtois', 32, 'GK', 1),
(2, 'Dani Carvajal', 33, 'DEF', 1),
(3, 'Jude Bellingham', 21, 'MIDF', 1),
(4, 'Robert Lewandowski', 36, 'STR', 2),
(5, 'Antoine Griezmann', 33, 'STR', 3);
