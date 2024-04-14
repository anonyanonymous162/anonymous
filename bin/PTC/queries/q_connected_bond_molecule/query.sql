
.mode csv 
.separator ,
.header on
.import bond.csv bond
.import connected.csv connected
.import molecule.csv molecule

select * 
from connected c, bond b, molecule m
where (c.bond_id = b.bond_id) and (b.molecule_id = m.molecule_id);


-- Auxiliary queries that are used for building the witness matrix

create view connected_rp as select (rowid -1) as id, * from connected;
create view bond_rp as select (rowid -1) as id, * from bond;
create view molecule_rp as select (rowid -1) as id, * from molecule;

.header off

.output ./w_connected.csv
select c.id 
from connected_rp c, bond_rp b, molecule_rp m
where (c.bond_id = b.bond_id) and (b.molecule_id = m.molecule_id);

.output ./w_bond.csv
select b.id 
from connected_rp c, bond_rp b, molecule_rp m
where (c.bond_id = b.bond_id) and (b.molecule_id = m.molecule_id);

.output ./w_molecule.csv
select m.id 
from connected_rp c, bond_rp b, molecule_rp m
where (c.bond_id = b.bond_id) and (b.molecule_id = m.molecule_id);


.header on
.output ./results.csv
select * 
from connected c, bond b, molecule m
where (c.bond_id = b.bond_id) and (b.molecule_id = m.molecule_id);

.output stdout