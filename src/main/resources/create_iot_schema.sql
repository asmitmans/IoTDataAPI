-- Tabla company
CREATE TABLE company (
    id SERIAL PRIMARY KEY,
    company_name VARCHAR(100) NOT NULL,
    company_api_key VARCHAR(255) NOT NULL
);

-- Tabla location
CREATE TABLE location (
    id SERIAL PRIMARY KEY,
    company_id INTEGER NOT NULL REFERENCES company(id) ON DELETE CASCADE,
    location_name VARCHAR(100) NOT NULL,
    location_country VARCHAR(50),
    location_city VARCHAR(50),
    location_meta JSONB DEFAULT '{}'
);

-- Tabla sensor
CREATE TABLE sensor (
    id SERIAL PRIMARY KEY,
    location_id INTEGER NOT NULL REFERENCES location(id) ON DELETE CASCADE,
    sensor_name VARCHAR(100) NOT NULL,
    sensor_category VARCHAR(50),
    sensor_api_key VARCHAR(255) UNIQUE NOT NULL,
    sensor_meta JSONB DEFAULT '{}'
);

-- Tabla sensor_data
CREATE TABLE sensor_data (
    id SERIAL PRIMARY KEY,
    sensor_id INTEGER NOT NULL REFERENCES sensor(id) ON DELETE CASCADE,
    timestamp_ms BIGINT NOT NULL,
    value_name VARCHAR(50) NOT NULL,
    value DOUBLE PRECISION NOT NULL
);

-- Tabla de usuarios
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    alias VARCHAR(20),
    names VARCHAR(100),
    surnames VARCHAR(100)
);

-- Tabla de roles
CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

-- Tabla de user_roles
--  Relación muchos a muchos entre users y roles.
CREATE TABLE user_roles (
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id INTEGER NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- Tabla menu
CREATE TABLE menu (
    id_menu SERIAL PRIMARY KEY,
    id_father INTEGER NOT NULL,
    father_name VARCHAR(20) NOT NULL,
    item_name VARCHAR(25) NOT NULL,
    icon VARCHAR(100) NOT NULL,
    icon_fury VARCHAR(100),
    url VARCHAR(100) NOT NULL
);

-- Tabla menu_role
CREATE TABLE menu_role (
    id_menu INTEGER NOT NULL REFERENCES menu(id_menu) ON DELETE CASCADE,
    id_role INTEGER NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (id_menu, id_role)
);

-- ------------------------------------------------------------

-- Insertar rol ADMIN
INSERT INTO roles (name)
VALUES ('ADMIN')
ON CONFLICT (name) DO NOTHING;

-- Insertar usuario ADMIN
INSERT INTO users (username, password, enabled)
VALUES (
    'admin',
    '$2a$12$HlJDc8.E7vkoOUfm8CK1.O3VvmCbfZ1cwdFKM59roZf6zdJljOXwi',
    TRUE
)
ON CONFLICT (username) DO NOTHING;

-- Asignar rol ADMIN al usuario admin
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'admin' AND r.name = 'ADMIN'
ON CONFLICT DO NOTHING;

-- ------------------------------------------------------------
