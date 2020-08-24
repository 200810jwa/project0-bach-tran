DROP TABLE IF EXISTS users CASCADE;
CREATE TABLE users (
	id SERIAL PRIMARY KEY,
	username VARCHAR(30) NOT NULL,
	password VARCHAR(64) NOT NULL,
	firstname VARCHAR(255) NOT NULL,
	lastname VARCHAR(255) NOT NULL,
	phone VARCHAR(12) NOT NULL,
	email VARCHAR(255) NOT NULL,
	accounttype VARCHAR(10) NOT NULL
);

INSERT INTO users
(username, password, firstname, lastname, phone, email, accounttype)
VALUES 
('jane_doe', '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5', 'Jane', 'Doe', '000-000-0000', 'jane_doe@outlook.com', 'Customer'),
('admin', '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5', 'admin', 'n/a', '000-000-0000', 'admin@bank.com', 'Admin'),
('employee_test', 'cf80cd8aed482d5d1527d7dc72fceff84e6326592848447d2dc0b0e87dfc9a90', 'Bob', 'Smith', '000-000-0000', 'employee1@bank.com', 'Employee');

SELECT * FROM users;