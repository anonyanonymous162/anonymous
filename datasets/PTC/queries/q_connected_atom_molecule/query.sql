
.mode csv 
.separator ,
.header on
.import atom.csv atom
.import connected.csv connected
.import molecule.csv molecule

select * 
from connected c, atom a, molecule m
where (a.atom_id = c.atom_id) and (a.molecule_id = m.molecule_id);


-- Auxiliary queries that are used for building the witness matrix

create view connected_rp as select (rowid -1) as id, * from connected;
create view atom_rp as select (rowid -1) as id, * from atom;
create view molecule_rp as select (rowid -1) as id, * from molecule;

.header off

.output ./w_connected.csv
select c.id 
from connected_rp c, atom_rp a, molecule_rp m
where (a.atom_id = c.atom_id) and (a.molecule_id = m.molecule_id);

.output ./w_atom.csv
select a.id 
from connected_rp c, atom_rp a, molecule_rp m
where (a.atom_id = c.atom_id) and (a.molecule_id = m.molecule_id);

.output ./w_molecule.csv
select m.id 
from connected_rp c, atom_rp a, molecule_rp m
where (a.atom_id = c.atom_id) and (a.molecule_id = m.molecule_id);


.header on
.output ./results.csv
select * 
from connected c, atom a, molecule m
where (a.atom_id = c.atom_id) and (a.molecule_id = m.molecule_id);

.output stdout