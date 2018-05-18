CREATE TABLE IF NOT EXISTS `osm_nodes` (
	`id`	BIGINT NOT NULL UNIQUE,
	`latitude`	REAL NOT NULL,
	`longitude`	REAL NOT NULL,
	PRIMARY KEY(`id`)
);

CREATE TABLE IF NOT EXISTS `osm_node_tags` (
	`id`	BIGINT NOT NULL UNIQUE,
	`name`	VARCHAR(45) DEFAULT NULL,
	`highway`	VARCHAR(45) DEFAULT NULL,
	PRIMARY KEY(`id`)
);

CREATE TABLE IF NOT EXISTS `osm_node_mappings` (
	`internal_id`	INTEGER NOT NULL UNIQUE,
	`osm_id`	BIGINT NOT NULL UNIQUE,
	PRIMARY KEY(`internal_id`)
);

CREATE TABLE IF NOT EXISTS `osm_way_tags` (
	`id`	BIGINT NOT NULL UNIQUE,
	`name`	VARCHAR(45) DEFAULT NULL,
	`highway`	VARCHAR(45) DEFAULT NULL,
	`maxspeed`	INTEGER DEFAULT NULL,
	PRIMARY KEY(`id`)
);

CREATE TABLE IF NOT EXISTS `osm_way_mappings` (
	`internal_id`	INTEGER NOT NULL UNIQUE,
	`osm_id`	BIGINT NOT NULL UNIQUE,
	PRIMARY KEY(`internal_id`)
);