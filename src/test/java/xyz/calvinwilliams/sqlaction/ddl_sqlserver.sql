CREATE TABLE user_base
(
    id INTEGER NOT NULL PRIMARY KEY,
    name VARCHAR(45) NOT NULL,
    gender VARCHAR(3) NOT NULL,
    age INTEGER NOT NULL,
    address VARCHAR(100) NOT NULL,
    lvl INTEGER NOT NULL
);
CREATE INDEX user_base_idx1 ON user_base ( id ) ;
CREATE SEQUENCE user_base_seq_id ;

CREATE TABLE user_order
(
    id INTEGER NOT NULL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    item_name VARCHAR(45) NOT NULL,
	amount INTEGER NOT NULL,
    total_price NUMERIC(12,2) NOT NULL
);
CREATE INDEX user_order_idx1 ON user_order ( id ) ;
CREATE SEQUENCE user_order_seq_id ;
