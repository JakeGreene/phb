# --- !Ups

create table "CLASSES" ("CLASS_ID" INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,"NAME" VARCHAR NOT NULL);
create table "RACES" ("RACE_ID" INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,"NAME" VARCHAR NOT NULL);
create table "SPELLS" ("SPELL_ID" INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,"NAME" VARCHAR NOT NULL,"KIND" VARCHAR NOT NULL,"TIME" VARCHAR NOT NULL,"RANGE" VARCHAR NOT NULL,"COMPONENTS" VARCHAR NOT NULL,"DURATION" VARCHAR NOT NULL,"BODY" VARCHAR NOT NULL);

# --- !Downs

drop table "SPELLS";
drop table "RACES";
drop table "CLASSES";

