DROP TABLE IF EXISTS project_category;
DROP TABLE IF EXISTS material;
DROP TABLE IF EXISTS step;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS project;

CREATE TABLE project(
	project_id INT AUTO_INCREMENT NOT NULL,
	project_name VARCHAR(128) NOT NULL,
	estimated_hours DECIMAL(7,2),
	actual_hours DECIMAL(7,2),
	difficulty INT,
	notes TEXT,
	PRIMARY KEY (project_id)
);

CREATE TABLE category(
	category_id INT AUTO_INCREMENT NOT NULL,
	category_name VARCHAR(128) NOT NULL,
	PRIMARY KEY (category_id)
);

CREATE TABLE step(
	step_id INT AUTO_INCREMENT NOT NULL,
	project_id INT NOT NULL,
	step_text TEXT NOT NULL,
	step_order INT NOT NULL,
	PRIMARY KEY (step_id),
	FOREIGN KEY (project_id) REFERENCES project (project_id) ON DELETE CASCADE
);

CREATE TABLE material(
	material_id INT AUTO_INCREMENT NOT NULL,
	project_id INT NOT NULL,
	material_name VARCHAR(128) NOT NULL,
	num_required INT,
	cost DECIMAL(7,2),
	PRIMARY KEY (material_id),
	FOREIGN KEY (project_id) REFERENCES project(project_id) ON DELETE CASCADE
);

CREATE TABLE project_category(
	project_id INT NOT NULL,
	category_id INT NOT NULL,
	FOREIGN KEY (project_id) REFERENCES project(project_id) ON DELETE CASCADE,
	FOREIGN KEY (category_id) REFERENCES category(category_id) ON DELETE CASCADE,
	UNIQUE(project_id),
	UNIQUE(category_id)
);

--Add Test Values

insert into project (project_name,estimated_hours , actual_hours, difficulty, notes) values 
('Hang a door', 4.3, 3.65, 3, 'Use the door hangers from Home Depot');

insert into material (project_id, material_name, num_required, cost) 
values (1, "Door in frame", 1, 75.99);

insert into material (project_id, material_name, num_required, cost) 
values (1, "2-inch screws", 20, 1.09);

insert into material (project_id, material_name, num_required, cost) 
values (1, "Door Hangers", 2, 45.99);

insert into step (project_id,step_text, step_order)
values (1, 'Align hangers on opening side of door.', 1);

insert into step (project_id, step_text, step_order)
values (1, 'Screw hangers into frame', 2);

insert into category (category_id, category_name) values (1, 'Doors and Windows');
insert into category (category_id, category_name) values (2, 'Repairs');
insert into category (category_id, category_name) values (3, 'Gardening');

insert into project_category (project_id, category_id) values (1,1);
insert into project_category (project_id, category_id) values (1,2);

