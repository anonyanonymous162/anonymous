
-- This is a semi-join
-- The table drug is composed of a single attribute that participate in the join

select * 
from atm, drug
where atm.drug_id = drug.drug_id;


-- Auxiliary queries that are used for building the witness matrix

create view atm_rp as select (rowid -1) as id, * from atm;

.header off

.output ./w.csv
Select id from (select * from atm_rp a, drug d where a.drug_id = d.drug_id);

.header on
.output ./results.csv
select atm.* from atm, drug where atm.drug_id = drug.drug_id;

.output stdout