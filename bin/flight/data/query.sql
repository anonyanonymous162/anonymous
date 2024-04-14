
select * 
from active a, drug d, bond b
where (a.drug_id = d.drug_id) and (d.drug_id = b.drug_id);


-- Auxiliary queries that are used for building the witness matrix

create view active_rp as select (rowid -1) as id, * from active;
create view bond_rp as select (rowid -1) as id, * from bond;


.header off

.output ./w_bond.csv
select b.id from active_rp a, drug d, bond_rp b where (a.drug_id = d.drug_id) and (b.drug_id = d.drug_id) ;

.output ./w_active.csv
Select a.id from active_rp a, drug d, bond_rp b where (a.drug_id = d.drug_id) and (b.drug_id = d.drug_id);

.header on
.output ./results.csv
select a.*, b.*  from active a, drug d, bond b where (a.drug_id = d.drug_id) and (d.drug_id = b.drug_id);

.output stdout