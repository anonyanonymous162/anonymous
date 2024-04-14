CREATE TABLE molecule_num (
  molecule_id INTEGER PRIMARY KEY NOT NULL,
  label      Integer
);

CREATE TABLE atom_num (
  atom_id INTEGER PRIMARY KEY NOT NULL,
  molecule_id    Integer,
  Element integer
);

CREATE TABLE bond_num (
  bond_id INTEGER PRIMARY KEY NOT NULL,
  molecule_id    Integer,
  bond_type integer
);

CREATE TABLE connected_num (
  atom_id INTEGER,
  atom_id2    Integer,
  bond_id integer
);

