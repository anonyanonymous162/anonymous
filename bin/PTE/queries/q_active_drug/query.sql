
-- This is (also) a semi-join
-- The table drug is composed of a single attribute that participate in the join

select * 
from active, drug
where active.drug_id = drug.drug_id;


-- Auxiliary queries that are used for building the witness matrix

create view active_rp as select (rowid -1) as id, * from active;

.header off

.output ./w.csv
Select id from (select * from active_rp a, drug d where a.drug_id = d.drug_id);

.header on
.output ./results.csv
select active.* from active, drug where active.drug_id = drug.drug_id;

.output stdout