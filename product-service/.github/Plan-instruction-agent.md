2. products table
This table belongs in product_db, managed by ProductService.
Column	Type	Purpose
id	BIGINT	Primary key
name	VARCHAR(200)	Product name
sku	VARCHAR(100)	Unique stock-keeping unit
description	TEXT	Product details
price	DECIMAL(12,2)	Current selling price
stock_quantity	INT	Available stock
category	VARCHAR(100)	Product category
image_url	VARCHAR(500)	Product image URL
active	BOOLEAN	Whether product can be purchased
created_at	DATETIME	Creation timestamp
updated_at	DATETIME	Last update timestamp

CREATE TABLE products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL,
    sku VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    price DECIMAL(12,2) NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    category VARCHAR(100),
    image_url VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT chk_product_price CHECK (price >= 0),
    CONSTRAINT chk_product_stock CHECK (stock_quantity >= 0)
);