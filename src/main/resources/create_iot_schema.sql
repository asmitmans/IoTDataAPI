-- Tabla de usuarios
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);

-- Tabla de roles
CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

-- Tabla relación entre usuarios y roles
CREATE TABLE user_roles (
    user_id INT REFERENCES users(id) ON DELETE CASCADE,
    role_id INT REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- Insertar rol ADMIN
INSERT INTO roles (name) VALUES ('ADMIN') ON CONFLICT (name) DO NOTHING;

-- Insertar usuario ADMIN
INSERT INTO users (username, password, enabled)
VALUES ('admin', '$2a$12$HlJDc8.E7vkoOUfm8CK1.O3VvmCbfZ1cwdFKM59roZf6zdJljOXwi', TRUE)
ON CONFLICT (username) DO NOTHING;

-- Asignar rol ADMIN al usuario ADMIN
INSERT INTO user_roles (user_id, role_id)
VALUES (
    (SELECT id FROM users WHERE username = 'admin'),
    (SELECT id FROM roles WHERE name = 'ADMIN')
) ON CONFLICT DO NOTHING;

-- Tabla company
CREATE TABLE company (
    id SERIAL PRIMARY KEY,
    company_name VARCHAR(100) UNIQUE NOT NULL,
    company_api_key VARCHAR(255) UNIQUE NOT NULL
);

-- Tabla location
CREATE TABLE location (
    id SERIAL PRIMARY KEY,
    company_id INT REFERENCES company(id) ON DELETE CASCADE,
    location_name VARCHAR(100) NOT NULL,
    location_country VARCHAR(50),
    location_city VARCHAR(50),
    location_meta JSONB DEFAULT '{}'
);

-- Tabla sensor
CREATE TABLE sensor (
    id SERIAL PRIMARY KEY,
    location_id INT REFERENCES location(id) ON DELETE CASCADE,
    sensor_name VARCHAR(100) NOT NULL,
    sensor_category VARCHAR(50),
    sensor_api_key VARCHAR(255) UNIQUE NOT NULL,
    sensor_meta JSONB DEFAULT '{}'
);

-- Tabla sensor_data
CREATE TABLE sensor_data (
    id SERIAL PRIMARY KEY,
    sensor_id INT REFERENCES sensor(id) ON DELETE CASCADE,
    timestamp BIGINT NOT NULL,
    value_name VARCHAR(50) NOT NULL,
    value DOUBLE PRECISION NOT NULL
);
