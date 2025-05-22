CREATE SCHEMA IF NOT EXISTS yacic;
SET SCHEMA yacic;
CREATE TABLE projects (
	project_id VARCHAR(32) NOT NULL,
	repo VARCHAR(256) NOT NULL
);
CREATE TABLE branches (
	project_id VARCHAR(32) NOT NULL,
	branch_id VARCHAR(128) NOT NULL,
	branchdir VARCHAR(32) NOT NULL
);
CREATE TABLE builds (
	project_id VARCHAR(32) NOT NULL,
	branch_id VARCHAR(32) NOT NULL,
	timestamp VARCHAR(14) NOT NULL,
	duration integer,
	build_id integer NOT NULL,
	status VARCHAR(32)
);
CREATE TABLE steps (
	project_id VARCHAR(32) NOT NULL,
	branch_id VARCHAR(32) NOT NULL,
	timestamp VARCHAR(14) NOT NULL,
	step_id VARCHAR(32) NOT NULL,
	seq integer NOT NULL,
	duration integer,
	status VARCHAR(32)
);

