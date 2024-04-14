
select * 
from atm a1, atm a2, drug d, bond b
where (a1.atom_id = b.atom_id1) and (a2.atom_id = b.atom_id2) and (b.drug_id = d.drug_id);


-- Auxiliary queries that are used for building the witness matrix

create view atm1_rp as select (rowid -1) as id, * from atm;
create view atm2_rp as select (rowid -1) as id, * from atm;
create view bond_rp as select (rowid -1) as id, * from bond;



.header off

.output ./w_atom1.csv
select a1.id 
from atm1_rp a1, atm2_rp a2, drug d, bond_rp b
where (a1.atom_id = b.atom_id1) and (a2.atom_id = b.atom_id2) and (b.drug_id = d.drug_id);

.output ./w_atom2.csv
select a2.id 
from atm1_rp a1, atm2_rp a2, drug d, bond_rp b
where (a1.atom_id = b.atom_id1) and (a2.atom_id = b.atom_id2) and (b.drug_id = d.drug_id);

.output ./w_bond.csv
select b.id 
from atm1_rp a1, atm2_rp a2, drug d, bond_rp b
where (a1.atom_id = b.atom_id1) and (a2.atom_id = b.atom_id2) and (b.drug_id = d.drug_id);




.header on
.output ./results.csv
select a1.*, b.*, a2.*  from atm a1, atm a2, drug d, bond b
where (a1.atom_id = b.atom_id1) and (a2.atom_id = b.atom_id2) and (b.drug_id = d.drug_id);


.output stdout