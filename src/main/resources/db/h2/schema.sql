-- Drop table if exists
DROP TABLE IF EXISTS users;

-- Create table
CREATE TABLE users
(
    idx             BIGINT AUTO_INCREMENT PRIMARY KEY,                                 -- Auto-increment primary key
    email           VARCHAR(255) NOT NULL UNIQUE,                                      -- Unique email
    password        VARCHAR(255) NOT NULL,                                             -- Password
    name            VARCHAR(100) NOT NULL,                                             -- User name
    status          VARCHAR(50) DEFAULT 'ACTIVE',                                      -- Status (default to 'ACTIVE')
    created_at      TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,                             -- Creation timestamp
    updated_at      TIMESTAMP   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- Last update timestamp
    last_login_ip   VARCHAR(16),                                                       -- Last login device ip
    last_login_at   TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,                             -- Last login timestamp
    failed_attempts INT         DEFAULT 0                                              -- Login failed attempts (default to 0)
);
