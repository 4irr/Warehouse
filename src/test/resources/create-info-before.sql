delete from acceptance;
delete from operation;
delete from user;
delete from product;
delete from batch;

insert into user(userid, username, password, role, activity, is_blocked) values
(1, 'admin', '$2a$10$3j45L2zunr/d8eViTVnM5.aUTF0PyxloyMKKI0aVJ9O.59s8FSGP.', 'admin', 0, 0),
(2, 'user1', '$2a$10$3j45L2zunr/d8eViTVnM5.aUTF0PyxloyMKKI0aVJ9O.59s8FSGP.', 'user', 0, 0);

insert into batch(batch_id, amount, type, weight) values
(1, 20, 'Ноутбуки', 25),
(2, 30, 'Планшеты', 35),
(3, 10, 'Велосипеды', 150);

insert into product(product_id, cell, name, price, shelf_life, batch_batch_id) values
(1, 'A1', 'Ноутбук', 1500, '2023.05.20', 1);

insert into operation(operation_id, date, type, user_userid) values
(1, '2023.05.07', 'Приём', '1');

insert into acceptance(acceptance_id, price, sender, batch_batch_id, operation_operation_id) values
(1, 2000, 'ООО ИКС', 1, 1);