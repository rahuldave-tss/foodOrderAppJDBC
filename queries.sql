CREATE TYPE user_role AS ENUM (
    'CUSTOMER',
    'DELIVERY_PARTNER',
    'ADMIN'
);

CREATE TYPE order_status AS ENUM (
    'CREATED',
    'PENDING',
    'ASSIGNED',
    'DELIVERED'
);

CREATE TYPE payment_type_enum AS ENUM (
    'CASH',
    'UPI'
);

CREATE TABLE users (
	id SERIAL PRIMARY KEY,
	name VARCHAR(50) NOT NULL,
	user_name VARCHAR(50) NOT NULL UNIQUE,
	password VARCHAR(50) NOT NULL,
	phone_number VARCHAR(10) NOT NULL UNIQUE,
	email VARCHAR(50) NOT NULL UNIQUE,
	role user_role NOT NULL
);

CREATE TABLE customer (
    user_id INT PRIMARY KEY,
    address TEXT,
    
    CONSTRAINT fk_customer_user
    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE
);

CREATE TABLE delivery_partner (
    user_id INT PRIMARY KEY,
    is_available BOOLEAN DEFAULT TRUE,

    CONSTRAINT fk_delivery_user
    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE
);

CREATE TABLE admin (
    user_id INT PRIMARY KEY,

    CONSTRAINT fk_admin_user
    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE
);

------------------------------------------------
CREATE TABLE food_item(
	id SERIAL PRIMARY KEY,
	name VARCHAR(20) NOT NULL UNIQUE,
	price NUMERIC(5) NOT NULL
);

CREATE TABLE cart(
	id SERIAL PRIMARY KEY,
	customer_id INT NOT NULL,

	CONSTRAINT fk_cart_customer
	FOREIGN KEY (customer_id)
	REFERENCES customer(user_id)
	ON DELETE CASCADE
);

ALTER TABLE cart
ADD CONSTRAINT unique_customer_cart
UNIQUE(customer_id);

CREATE TABLE cart_item (
    id SERIAL PRIMARY KEY,
    cart_id INT NOT NULL,
    food_item_id INT NOT NULL,
    quantity INT NOT NULL,

    CONSTRAINT fk_cartitem_cart
    FOREIGN KEY (cart_id)
    REFERENCES cart(id)
    ON DELETE CASCADE,

    CONSTRAINT fk_cartitem_food
    FOREIGN KEY (food_item_id)
    REFERENCES food_item(id)
);

CREATE TABLE discount (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100),
    discount_amount NUMERIC(10,2),
    discount_percentage NUMERIC(5,2)
);

CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    customer_id INT NOT NULL,
    delivery_partner_id INT,
    discount_id INT,
    final_amount NUMERIC(10,2),
    status order_status,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_order_customer
    FOREIGN KEY (customer_id)
    REFERENCES customer(user_id),

    CONSTRAINT fk_order_delivery
    FOREIGN KEY (delivery_partner_id)
    REFERENCES delivery_partner(user_id),

    CONSTRAINT fk_order_discount
    FOREIGN KEY (discount_id)
    REFERENCES discount(id)
);


CREATE TABLE order_item (
    id SERIAL PRIMARY KEY,
    order_id INT NOT NULL,
    food_item_id INT NOT NULL,
    quantity INT NOT NULL,
    order_item_price NUMERIC(10,2),

    CONSTRAINT fk_orderitem_order
    FOREIGN KEY (order_id)
    REFERENCES orders(id)
    ON DELETE CASCADE,

    CONSTRAINT fk_orderitem_food
    FOREIGN KEY (food_item_id)
    REFERENCES food_item(id)
);

CREATE TABLE payment (
    id SERIAL PRIMARY KEY,
    order_id INT UNIQUE,
    payment_type payment_type_enum,
    amount NUMERIC(10,2),
    paid_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_payment_order
    FOREIGN KEY (order_id)
    REFERENCES orders(id)
    ON DELETE CASCADE
);

SELECT * FROM users;

--admin
INSERT INTO users(name,user_name,password,phone_number,email,role)
VALUES ('Rahul','rahul123','123','9429440193','rahul.dave@tssconsultancy.com','ADMIN');
--menu items
INSERT INTO food_item(name,price)
VALUES ('Burger',100),('Pizza',200),('Pasta',150);

SELECT * FROM users;
SELECT * FROM users;
SELECT * FROM customer;

Select c.*,u.* from customer c JOIN users u ON c.user_id=u.id;

