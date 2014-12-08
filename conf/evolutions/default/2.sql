# --- !Ups

CREATE TABLE class_spells (class_id INTEGER REFERENCES classes(class_id),
                           spell_id INTEGER REFERENCES spells(spell_id),
                           level INTEGER NOT NULL,
                           PRIMARY KEY(class_id, spell_id));

# --- !Downs

DROP TABLE class_spells;