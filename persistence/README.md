# Persistence

We will create a Postgresql database for yacic.

* a role *yacic*
* a database *yacic*
* the tables

i had to grant explicitely the permissions to the tables:

    grant all on table  projects to yacic;
    grant all on table  branches to yacic;
    grant all on table  builds to yacic;
    grant all on table  steps to yacic;