DROP TABLE IF EXISTS bankaccounts CASCADE;
CREATE TABLE bankaccounts (
	id SERIAL PRIMARY KEY,
	balance NUMERIC(50,2) DEFAULT 0
);

INSERT INTO bankaccounts (balance) VALUES 
(0),
(0),
(0),
(0);

SELECT * FROM bankaccounts