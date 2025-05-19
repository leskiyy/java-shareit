--test db init data
INSERT INTO users (id, name, email)
VALUES  (1, 'name1', 'email1'),
        (2, 'name2', 'email2'),
        (3, 'name3', 'email3'),
        (4, 'name4', 'email4');
SELECT SETVAL('users_id_seq', (SELECT MAX(id) FROM users));

INSERT INTO item_request (id, description, created, author_id)
VALUES  (1, 'desc1', '2025-05-25 19:00:00.000', 1),
        (2, 'desc2', '2025-05-25 19:10:00.000', 1),
        (3, 'desc3', '2025-05-25 19:10:00.000', 2);
SELECT SETVAL('item_request_id_seq', (SELECT MAX(id) FROM item_request));

INSERT INTO item (id, name, description, available, owner_id, item_request_id)
VALUES  (1,	'item1', 'desc1', true, 1, null),
        (2,	'item2', 'desc2', true, 1, 3),
        (3,	'item3', 'desc3', true, 2, 1),
        (4,	'item4', 'desc4', false, 3, null),
        (5,	'item5 target', 'desc5', true, 3, null),
        (6,	'item6', 'desc6 target', true, 3, 1);
SELECT SETVAL('item_id_seq', (SELECT MAX(id) FROM item));

INSERT INTO booking (id, start_time, end_time, status, item_id, booker_id)
VALUES  (1,	'2021-05-26 19:00:00.000', '2021-05-27 19:00:00.000', 'WAITING', 1, 3),
        (2,	'2035-05-26 19:00:00.000', '2035-05-27 19:00:00.000', 'APPROVED', 1, 2),
        (3,	'2024-05-26 19:00:00.000', '2024-05-27 19:00:00.000', 'APPROVED', 2, 2),
        (4,	'2024-05-26 19:00:00.000', '2024-05-27 19:00:00.000', 'CANCELED', 4, 1),
        (5,	'2024-05-26 19:00:00.000', '2024-05-27 19:00:00.000', 'REJECTED', 4, 1);
SELECT SETVAL('booking_id_seq', (SELECT MAX(id) FROM booking));


INSERT INTO comment (id, text, created, item_id, author_id)
VALUES  (1,	'text1', '2021-05-27 19:00:00.000', 1, 1),
        (2,	'text2', '2022-05-27 19:00:00.000', 2, 2),
        (3,	'text3', '2022-04-27 19:00:00.000', 4, 3),
        (4,	'text4', '2023-05-27 19:00:00.000', 4, 3),
        (5,	'text5', '2024-02-27 19:00:00.000', 4, 1);
SELECT SETVAL('comment_id_seq', (SELECT MAX(id) FROM comment));