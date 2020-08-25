-- Users table creation
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
('admin', '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5', 'John', 'Doe', '512-271-1622', 'admin@bank.com', 'Admin'),
('employee1', 'b822f1cd2dcfc685b47e83e3980289fd5d8e3ff3a82def24d7d1d68bb272eb32', 'Bob', 'Smith', '512-271-7568', 'employee1@bank.com', 'Employee'),
('bach_tran', '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5', 'Bach', 'Tran', '512-826-2486', 'bach_tran@outlook.com', 'Customer'),
('jane_doe', '166ba22f5313a8d0df0b41fc19e5dfefd91ddc5d16e8b7eb071db111ed2b196e', 'Jane', 'Doe', '512-271-1623', 'jane_doe@outlook.com', 'Customer'),
('john_doe', '4663fe0c1d68b2e8cd1a6c1df668e637c99ca34002e1d126d944aecd45179abf', 'John', 'Doe', '512-826-2487', 'john_doe@outlook.com', 'Customer');

-- Bank Accounts table creation
DROP TABLE IF EXISTS bankaccounts CASCADE;
CREATE TABLE bankaccounts (
	id SERIAL PRIMARY KEY,
	balance NUMERIC(50,2) DEFAULT 0
);

INSERT INTO bankaccounts 
(balance) 
VALUES
(0),
(0),
(0),
(0);

-- user_account_join Junction table
DROP TABLE IF EXISTS user_account_join CASCADE;
CREATE TABLE user_account_join (
	user_id INT,
	account_id INT,
	approved boolean DEFAULT false,
	pending boolean DEFAULT true,
	CONSTRAINT no_duplicate_application
		UNIQUE(user_id, account_id),
	CONSTRAINT fk_user
		FOREIGN KEY (user_id) REFERENCES users(id),
	CONSTRAINT fk_accounts
      FOREIGN KEY (account_id)
      REFERENCES bankaccounts (id)
      ON DELETE CASCADE
);


INSERT INTO user_account_join 
(user_id, account_id, approved, pending)
VALUES
(4, 2, false, true),
(5, 2, false, true),
(1, 3, false, true),
(2, 4, false, true);

-- transfer_funds Function
CREATE OR REPLACE FUNCTION transfer_funds (
	source_account INTEGER,
	target_account INTEGER,
	amount NUMERIC(50, 2)) RETURNS boolean
AS 
$$
BEGIN
	IF amount <= 0
		THEN RAISE EXCEPTION 'INVALID AMOUNT';
	END IF;
	
	IF NOT withdraw(source_account, amount) 
		THEN RAISE EXCEPTION 'COULD NOT WITHDRAW'; 
	END IF;
		
	IF NOT withdraw(target_account, -1 * amount)
		THEN RAISE EXCEPTION 'COULD NOT DEPOSIT';
	END IF;

	RETURN TRUE;
EXCEPTION
	WHEN OTHERS THEN
		RETURN FALSE;
END;
$$ LANGUAGE plpgsql;

-- Withdraw function
CREATE OR REPLACE FUNCTION withdraw(account_id INTEGER, amount NUMERIC(50,2))
	RETURNS BOOLEAN
as 
$$
BEGIN
	IF amount > (SELECT balance FROM bankaccounts WHERE id = account_id) THEN
		RETURN false;
	ELSIF account_id NOT IN (SELECT id FROM bankaccounts WHERE id = account_id) THEN
		RETURN false;
	ELSE
		UPDATE bankaccounts SET balance = balance - amount
			WHERE id = account_id;
		return true;
	END IF;
END;
$$ LANGUAGE plpgsql;