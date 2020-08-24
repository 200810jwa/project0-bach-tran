DROP TABLE IF EXISTS user_account_join CASCADE;
CREATE TABLE user_account_join (
	user_id INT,
	account_id INT,
	approved boolean DEFAULT false,
	pending boolean DEFAULT true,
	CONSTRAINT fk_user
		FOREIGN KEY (user_id) REFERENCES users(id),
	CONSTRAINT fk_accounts
		FOREIGN KEY (account_id) REFERENCES bankaccounts(id)
);

INSERT INTO user_account_join (user_id, account_id, approved, pending) VALUES 
(1, 1, false, false),
(2, 2, false, true),
(3, 3, false, true),
(1, 4, false, true);