CREATE table if not exists public.users (
	id SERIAL PRIMARY KEY,
	"name" varchar(128) NOT NULL,
	email varchar(128) NOT NULL,
	CONSTRAINT users_email_key UNIQUE (email)
);

CREATE table if not exists public.item (
	id SERIAL PRIMARY KEY,
	"name" varchar(128) NOT NULL,
	description varchar(255) NOT NULL,
	available bool NOT NULL,
	owner_id int8 NOT NULL,
	CONSTRAINT items_users_fk FOREIGN KEY (owner_id) REFERENCES public.users(id) ON DELETE CASCADE
);

CREATE TABLE if not exists public.booking (
	id SERIAL PRIMARY KEY,
	"start_time" timestamp without time zone NOT NULL,
	"end_time" timestamp without time zone NOT NULL,
	status varchar(8) NOT NULL,
	item_id int8 NOT NULL,
	booker_id int8 NOT NULL,
	CONSTRAINT booking_check CHECK (status in ('WAITING', 'APPROVED', 'REJECTED', 'CANCELED')),
	CONSTRAINT booking_items_fk FOREIGN KEY (item_id) REFERENCES public.item(id) ON DELETE CASCADE,
	CONSTRAINT booking_users_fk FOREIGN KEY (booker_id) REFERENCES public.users(id) ON DELETE CASCADE
);

CREATE TABLE if not exists public."comment" (
	id SERIAL PRIMARY KEY,
	"text" varchar(255) NOT NULL,
	created timestamp without time zone NOT NULL,
	author_id int8 NOT NULL,
	item_id int8 NOT NULL,
	CONSTRAINT comments_items_fk FOREIGN KEY (item_id) REFERENCES public.item(id) ON DELETE CASCADE,
	CONSTRAINT comments_users_fk FOREIGN KEY (author_id) REFERENCES public.users(id) ON DELETE CASCADE
);
