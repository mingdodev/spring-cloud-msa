CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(20),
    pwd VARCHAR(20),
    name VARCHAR(20),
    created_at DATETIME DEFAULT NOW()
    );

CREATE TABLE IF NOT EXISTS orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id VARCHAR(20) NOT NULL,
    qty INT DEFAULT 0,
    unit_price INT DEFAULT 0,
    total_price INT DEFAULT 0,
    user_id VARCHAR(50) NOT NULL,
    order_id VARCHAR(50) NOT NULL,
    created_at DATETIME DEFAULT NOW()
    );
