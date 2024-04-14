# sage
Agree-sets Propagation to Views


This repository contains two folders:

The datasets folder, contains informagtion about the datasets used in the 
empirical evaluation.

The Sage folder is an eclipse java project.

The main classes are:

BaseTable: Which given a relational table (csv file), generate the 
relation evidence matrix.

JoinTable: It is used to generate the query evidence matrix, given the 
evidence matrices of the base relations involved in the query.

The dataset folder contains more information about how other inputs that 
are necessary given a dataset; In particular, it shows how SQL can be 
instrumented to create the witness matrix of a relation with respect to a 
query during the query evaluation. 
