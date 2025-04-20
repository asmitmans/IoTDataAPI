CREATE TABLE company (
    id SERIAL PRIMARY KEY,
    company_name VARCHAR(100) NOT NULL,
    company_api_key VARCHAR(255) NOT NULL
);

CREATE TABLE location (
    id SERIAL PRIMARY KEY,
    company_id INTEGER NOT NULL REFERENCES company(id) ON DELETE CASCADE,
    location_name VARCHAR(100) NOT NULL,
    location_country VARCHAR(50),
    location_city VARCHAR(50),
    location_meta JSONB DEFAULT '{}'
);

CREATE TABLE sensor (
    id SERIAL PRIMARY KEY,
    location_id INTEGER NOT NULL REFERENCES location(id) ON DELETE CASCADE,
    sensor_name VARCHAR(100) NOT NULL,
    sensor_category VARCHAR(50),
    sensor_api_key VARCHAR(255) UNIQUE NOT NULL,
    sensor_meta JSONB DEFAULT '{}'
);

CREATE TABLE sensor_data (
    id SERIAL PRIMARY KEY,
    sensor_id INTEGER NOT NULL REFERENCES sensor(id) ON DELETE CASCADE,
    timestamp_s BIGINT NOT NULL,
    value_name VARCHAR(50) NOT NULL,
    value DOUBLE PRECISION NOT NULL
);

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    alias VARCHAR(20),
    names VARCHAR(100),
    surnames VARCHAR(100),
    company_id INTEGER REFERENCES company(id) ON DELETE SET NULL
);

CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE user_roles (
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id INTEGER NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE menu (
    id_menu SERIAL PRIMARY KEY,
    id_father INTEGER NOT NULL,
    father_name VARCHAR(20) NOT NULL,
    item_name VARCHAR(25) NOT NULL,
    icon VARCHAR(100) NOT NULL,
    icon_fury VARCHAR(100),
    url VARCHAR(100) NOT NULL
);

CREATE TABLE menu_role (
    id_menu INTEGER NOT NULL REFERENCES menu(id_menu) ON DELETE CASCADE,
    id_role INTEGER NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (id_menu, id_role)
);


-- Roles
INSERT INTO roles (name) VALUES ('ADMIN');
INSERT INTO roles (name) VALUES ('USER');

-- Empresa
INSERT INTO company (company_api_key, company_name)
VALUES ('b144efd5-defd-4cb8-be0f-893b7048b4a5', 'testcompany');

-- Usuario admin
INSERT INTO users (username, password, enabled)
VALUES (
    'admin',
    '$2a$12$HlJDc8.E7vkoOUfm8CK1.O3VvmCbfZ1cwdFKM59roZf6zdJljOXwi',
    TRUE
);

-- Usuario testuser (con company_id dinámico)
INSERT INTO users (username, password, enabled, company_id)
SELECT
    'testuser',
    '$2y$10$UGGZSJ1rZj6Fj6xitaZ2D.v8tNixiqcxWfMtxCcXlfH4vxivV9a16',
    TRUE,
    id
FROM company
WHERE company_api_key = 'b144efd5-defd-4cb8-be0f-893b7048b4a5';

-- Asignación de roles
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'admin' AND r.name = 'ADMIN';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'testuser' AND r.name = 'USER';

-- Menús
INSERT INTO menu(id_menu, id_father, father_name, item_name, icon, icon_fury, url) VALUES
(1, 1, 'Home', 'Dashboard', 'pi pi-fw pi-home', 'dashboard', '/dashboard'),
(2, 2, 'Search', 'Sensor Data', 'pi pi-fw pi-arrow-right-arrow-left', 'search', '/search/sensor-data'),
(3, 3, 'Data', 'Companies', 'pi pi-fw pi-id-card', 'factory', '/pages/data/companies'),
(4, 3, 'Data', 'Locations', 'pi pi-fw pi-id-card', 'pin_drop', '/pages/data/locations'),
(5, 3, 'Data', 'Sensors', 'pi pi-fw pi-id-card', 'sensors', '/pages/data/sensors');

-- Asignación de menús a rol ADMIN (id_role = 1)
INSERT INTO menu_role (id_menu, id_role) VALUES
(1, 1),
(2, 1),
(3, 1),
(4, 1),
(5, 1);