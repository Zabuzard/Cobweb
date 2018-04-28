CREATE TABLE IF NOT EXISTS `osm_nodes` (
	`id`	BIGINT NOT NULL UNIQUE,
	`latitude`	DOUBLE NOT NULL,
	`longitude`	DOUBLE NOT NULL,
	PRIMARY KEY(`id`)
);

CREATE TABLE IF NOT EXISTS `osm_node_tags` (
	`id`	BIGINT NOT NULL UNIQUE,
	`name`	VARCHAR(45) DEFAULT NULL,
	`highway`	VARCHAR(45) DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS `osm_way_nodes` (
	`way_id`	BIGINT NOT NULL,
	`node_id`	BIGINT NOT NULL,
	PRIMARY KEY(`way_id`)
);

CREATE TABLE IF NOT EXISTS `osm_way_tags` (
	`id`	BIGINT NOT NULL UNIQUE,
	`name`	VARCHAR(45) DEFAULT NULL,
	`highway`	VARCHAR(45) DEFAULT NULL,
	`maxspeed`	INTEGER DEFAULT NULL,
	PRIMARY KEY(`id`)
);