
select * 
from atom a, molecule m
where a.molecule_id = m.molecule_id;


-- Auxiliary queries that are used for building the witness matrix

create view atom_rp as select (rowid -1) as id, * from atom;
create view molecule_rp as select (rowid -1) as id, * from molecule;

.header off

.output ./w_atom.csv
select a.id 
from atom_rp a, molecule_rp m
where a.molecule_id = m.molecule_id;

.output ./w_molecule.csv
select m.id 
from atom_rp a, molecule_rp m
where a.molecule_id = m.molecule_id;

.header on
.output ./results.csv
select * 
from atom a, molecule m
where a.molecule_id = m.molecule_id;

.output stdout