CREATE SCHEMA IF NOT EXISTS yacic;
SET SCHEMA yacic;
CREATE TABLE pipelines (
	pipeline_id VARCHAR(32) NOT NULL, 
	id VARCHAR(32) NOT NULL,
	status VARCHAR(32),
	start_date VARCHAR(32),
	end_date VARCHAR(32),
	current_step VARCHAR(32)
);
