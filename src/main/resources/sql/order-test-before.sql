insert into customer(id, name)
values('TEST_USER_ID', 'TEST_USER_NAME');
insert into customer(id, name)
values('NEW_TEST_USER_ID', 'NEW_TEST_USER_NAME');

insert into product(id, name, price)
values('PROD1', 'Computer', 8000.00);

insert into product(id, name, price)
values('PROD2', 'Keyboard', 1000.00);

insert into product(id, name, price)
values('PROD3', 'Memory', 1100.00);

insert into sale_order(id, create_time, customer_id, total_price, total_payment, status, version)
values('TEST_ORDER', '2019-09-20 09:00:01', 'TEST_USER_ID', 9000.00, 0.00, 0, 1);

insert into order_item(id, order_id, prod_id, amount, sub_total)
VALUES(1, 'TEST_ORDER', 'PROD1', 1.0, 8000.00);
insert into order_item(id, order_id, prod_id, amount, sub_total)
VALUES(2, 'TEST_ORDER', 'PROD2', 1.0, 1000.00);

