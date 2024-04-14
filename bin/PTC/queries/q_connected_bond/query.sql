
select * 
from connected c, bond b
where c.bond_id = b.bond_id;


-- Auxiliary queries that are used for building the witness matrix

create view connected_rp as select (rowid -1) as id, * from connected;
create view bond_rp as select (rowid -1) as id, * from bond;

.header off

.output ./w_connected.csv
select c.id 
from connected_rp c, bond_rp b
where c.bond_id = b.bond_id;

.output ./w_bond.csv
select b.id 
from connected_rp c, bond_rp b
where c.bond_id = b.bond_id;


.header on
.output ./results.csv
select * 
from connected c, bond b
where c.bond_id = b.bond_id;

.output stdout