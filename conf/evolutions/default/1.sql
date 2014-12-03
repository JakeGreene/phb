# --- !Ups

CREATE TABLE classes (class_id SERIAL PRIMARY KEY, 
                      name TEXT NOT NULL);
CREATE TABLE races (race_id SERIAL PRIMARY KEY, 
                    name TEXT NOT NULL);
CREATE TABLE spells (spell_id SERIAL PRIMARY KEY, 
                     name TEXT NOT NULL, 
                     kind TEXT NOT NULL, 
                     time TEXT NOT NULL, 
                     range TEXT NOT NULL, 
                     components TEXT NOT NULL, 
                     duration TEXT NOT NULL, 
                     body TEXT NOT NULL);

# --- !Downs

drop table spells;
drop table races;
drop table classes;

