CREATE TABLE "user_base"
(
    id integer NOT NULL,
    name character varying(45) NOT NULL,
    gender character varying(3) NOT NULL,
    age smallint NOT NULL,
    address character varying(100) NOT NULL,
    level integer NOT NULL,
    CONSTRAINT user_base_pkey PRIMARY KEY (id)
);
CREATE INDEX user_base_idx1 ON "user_base" USING btree (name) ;

CREATE SEQUENCE user_base_seq_id ;

CREATE TABLE "user_order"
(
    id integer NOT NULL,
    user_id integer NOT NULL,
    item_name character varying(45) NOT NULL,
    total_price numeric(12,2) NOT NULL,
    CONSTRAINT user_order_pkey PRIMARY KEY (id)
);
CREATE INDEX user_order_idx1 ON user_order USING btree (user_id) ;
