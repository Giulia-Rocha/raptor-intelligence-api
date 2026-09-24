-- ============================================================
-- RAPTOR INTELLIGENCE — PostgreSQL Schema
-- App de inteligência competitiva para Ford Ranger Raptor
-- ============================================================

-- Extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================================
-- OBSERVAÇÃO IMPORTANTE
-- ============================================================
-- As colunas categóricas (fuel_type, category, etc.) são VARCHAR com
-- CHECK em vez de ENUM do Postgres. Motivo: o Hibernate envia esses
-- valores como VARCHAR via JDBC, e um ENUM do Postgres rejeita o cast
-- implícito, quebrando os INSERT da API (erro: "column ... is of type
-- ... but expression is of type character varying").

-- ============================================================
-- BRANDS
-- ============================================================

CREATE TABLE brands (
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    country     VARCHAR(100),
    logo_url    TEXT,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- VEHICLES
-- ============================================================

CREATE TABLE vehicles (
    id           SERIAL PRIMARY KEY,
    brand_id     INT NOT NULL REFERENCES brands(id) ON DELETE RESTRICT,
    model        VARCHAR(100) NOT NULL,   -- ex: "Ranger", "Colorado", "Shark"
    version      VARCHAR(150) NOT NULL,   -- ex: "Raptor V6 EcoBoost", "ZR2 Bison"
    model_year   SMALLINT,
    fuel_type    VARCHAR(20) NOT NULL,
    category     VARCHAR(30) NOT NULL,
    is_reference BOOLEAN NOT NULL DEFAULT FALSE, -- TRUE = Ford Ranger Raptor (baseline)
    image_url    TEXT,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_vehicle UNIQUE (brand_id, model, version, model_year),
    CONSTRAINT chk_vehicle_fuel_type CHECK (fuel_type IN ('gasolina', 'diesel', 'eletrico', 'hibrido', 'phev')),
    CONSTRAINT chk_vehicle_category CHECK (category IN ('desert_runner', 'rock_crawler', 'diesel_global', 'full_size', 'hybrid_disruptor'))
);

-- Garante apenas 1 veículo de referência
CREATE UNIQUE INDEX uq_reference_vehicle
    ON vehicles (is_reference)
    WHERE is_reference = TRUE;

-- ============================================================
-- ENGINE SPECS (1:1 com vehicles)
-- ============================================================

CREATE TABLE engine_specs (
    id                          SERIAL PRIMARY KEY,
    vehicle_id                  INT NOT NULL UNIQUE REFERENCES vehicles(id) ON DELETE CASCADE,

    -- Motor
    engine_type                 VARCHAR(200),   -- ex: "3.0L V6 EcoBoost Twin-Turbo"
    displacement_liters         NUMERIC(4, 2),
    cylinders                   SMALLINT,
    cylinder_layout             VARCHAR(20),    -- ex: "V", "em linha"
    turbo_type                  VARCHAR(100),   -- ex: "twin-turbo", "geometria variável"

    -- Performance
    power_hp                    SMALLINT,
    torque_nm                   SMALLINT,
    torque_rpm                  SMALLINT,
    acceleration_0_100_s        NUMERIC(4, 1),
    top_speed_kmh               SMALLINT,

    -- Consumo
    fuel_consumption_urban_kml  NUMERIC(5, 2),  -- km/l
    fuel_consumption_hwy_kml    NUMERIC(5, 2),
    tank_capacity_liters        SMALLINT,
    estimated_range_km          SMALLINT,

    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- DRIVETRAIN SPECS (1:1 com vehicles)
-- ============================================================

CREATE TABLE drivetrain_specs (
    id                  SERIAL PRIMARY KEY,
    vehicle_id          INT NOT NULL UNIQUE REFERENCES vehicles(id) ON DELETE CASCADE,

    transmission_type   VARCHAR(20),
    gears               SMALLINT,
    traction_type       VARCHAR(20),
    has_low_range       BOOLEAN,
    diff_lock_type      VARCHAR(150),   -- ex: "Tru-Lok dianteiro e traseiro"
    has_hill_descent    BOOLEAN,
    has_hill_start      BOOLEAN,

    CONSTRAINT chk_transmission_type CHECK (transmission_type IN ('automatica', 'manual', 'cvt', 'e_cvt')),
    CONSTRAINT chk_traction_type CHECK (traction_type IN ('4x2', '4x4_part_time', '4x4_full_time', 'e_awd', 'awd')),
    -- JSON array de strings: ["Baja", "Normal", "Areia", "Neve"]
    drive_modes         JSONB,

    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- SUSPENSION SPECS (1:1 com vehicles)
-- ============================================================

CREATE TABLE suspension_specs (
    id                      SERIAL PRIMARY KEY,
    vehicle_id              INT NOT NULL UNIQUE REFERENCES vehicles(id) ON DELETE CASCADE,

    front_suspension        VARCHAR(200),   -- ex: "Independente Fox Live Valve"
    rear_suspension         VARCHAR(200),   -- ex: "Eixo rígido Multimatic DSSV"
    front_shock_brand       VARCHAR(100),   -- ex: "Fox", "Multimatic", "Bilstein"

    ground_clearance_mm     SMALLINT,
    approach_angle_deg      NUMERIC(4, 1),
    departure_angle_deg     NUMERIC(4, 1),
    water_crossing_mm       SMALLINT,

    tire_type               VARCHAR(100),   -- ex: "All-Terrain", "Mud-Terrain"
    tire_size_inches        SMALLINT,
    tire_spec               VARCHAR(50),    -- ex: "265/60 R18"

    offroad_profile         VARCHAR(100),   -- ex: "Desert Running", "Rock Crawling"

    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- DIMENSIONS (1:1 com vehicles)
-- ============================================================

CREATE TABLE dimensions (
    id                  SERIAL PRIMARY KEY,
    vehicle_id          INT NOT NULL UNIQUE REFERENCES vehicles(id) ON DELETE CASCADE,

    length_mm           SMALLINT,
    width_mm            SMALLINT,
    height_mm           SMALLINT,
    wheelbase_mm        SMALLINT,
    curb_weight_kg      SMALLINT,
    payload_kg          SMALLINT,
    towing_capacity_kg  SMALLINT,
    bed_volume_liters   SMALLINT,
    passenger_capacity  SMALLINT,

    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- WARRANTY (1:1 com vehicles)
-- ============================================================

CREATE TABLE warranty (
    id                          SERIAL PRIMARY KEY,
    vehicle_id                  INT NOT NULL UNIQUE REFERENCES vehicles(id) ON DELETE CASCADE,

    warranty_years              SMALLINT,
    powertrain_warranty_years   SMALLINT,
    battery_warranty_years      SMALLINT,       -- relevante para BYD Shark
    service_interval_km         SMALLINT,
    avg_service_cost_brl        NUMERIC(10, 2),
    has_roadside_assistance     BOOLEAN,
    warranty_notes              TEXT,

    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- FEATURES (catálogo de itens de equipamento)
-- ============================================================

CREATE TABLE features (
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(200) NOT NULL UNIQUE,    -- ex: "Apple CarPlay", "Câmera 360°"
    category    VARCHAR(20) NOT NULL,

    CONSTRAINT chk_feature_category CHECK (category IN ('conforto', 'conectividade', 'seguranca', 'tecnologia', 'exterior', 'pos_venda')),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- VEHICLE_FEATURES (N:M — vehicle ↔ feature com valor)
-- ============================================================

CREATE TABLE vehicle_features (
    vehicle_id  INT NOT NULL REFERENCES vehicles(id) ON DELETE CASCADE,
    feature_id  INT NOT NULL REFERENCES features(id) ON DELETE CASCADE,
    -- "Sim", "Não", "Opcional", "11,3 polegadas", "Bose 7 speakers", etc.
    value       VARCHAR(200) NOT NULL DEFAULT 'Sim',

    PRIMARY KEY (vehicle_id, feature_id)
);

-- ============================================================
-- SPEC CATEGORY MAP — mapeia cada spec dinamicamente por categoria.
-- A lógica do comparativo (/compare) é 100% dirigida por esta tabela.
-- field_name refere-se ao campo da tabela de origem em snake_case
-- (ou ao nome exato de uma feature da tabela `features`).
-- ============================================================

CREATE TABLE spec_category_map (
    id              SERIAL PRIMARY KEY,
    category_key    VARCHAR(50) NOT NULL,
    category_label  VARCHAR(100) NOT NULL,
    field_source    VARCHAR(50) NOT NULL,   -- tabela de origem
    field_name      VARCHAR(200) NOT NULL,  -- coluna ou nome da feature
    field_label     VARCHAR(200) NOT NULL,
    display_unit    VARCHAR(30),
    display_order   SMALLINT NOT NULL DEFAULT 1,
    radar_weight    NUMERIC(3, 2) NOT NULL DEFAULT 1.00,

    CONSTRAINT uq_spec_category_map UNIQUE (category_key, field_name)
);

CREATE INDEX idx_spec_category_map_cat ON spec_category_map (category_key);

-- ============================================================
-- APP USERS (consultores e administradores)
-- ============================================================

CREATE TABLE app_users (
    id            SERIAL PRIMARY KEY,
    name          VARCHAR(150) NOT NULL,
    email         VARCHAR(200) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    dealership    VARCHAR(150),
    role          VARCHAR(20) NOT NULL DEFAULT 'CONSULTOR',
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Perfis disponíveis: ADMIN, CONSULTOR.
-- Os usuários iniciais são criados em runtime pelo DataInitializer (Java),
-- usando BCrypt, para garantir hashes válidos.

-- ============================================================
-- COMPARISONS (histórico de comparações do app — por usuário)
-- ============================================================

CREATE TABLE comparisons (
    id              SERIAL PRIMARY KEY,
    user_id         INT NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
    vehicle_a_id    INT NOT NULL REFERENCES vehicles(id) ON DELETE CASCADE,
    vehicle_b_id    INT NOT NULL REFERENCES vehicles(id) ON DELETE CASCADE,
    notes           TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_different_vehicles CHECK (vehicle_a_id <> vehicle_b_id)
);

CREATE INDEX idx_comparisons_user   ON comparisons (user_id);
CREATE INDEX idx_comparisons_veh_a  ON comparisons (vehicle_a_id);
CREATE INDEX idx_comparisons_veh_b  ON comparisons (vehicle_b_id);

-- ============================================================
-- INDEXES
-- ============================================================

-- Filtros frequentes no app
CREATE INDEX idx_vehicles_category    ON vehicles (category);
CREATE INDEX idx_vehicles_fuel_type   ON vehicles (fuel_type);
CREATE INDEX idx_vehicles_brand_id    ON vehicles (brand_id);

-- Queries de comparação de performance
CREATE INDEX idx_engine_power         ON engine_specs (power_hp);
CREATE INDEX idx_engine_torque        ON engine_specs (torque_nm);
CREATE INDEX idx_engine_acceleration  ON engine_specs (acceleration_0_100_s);

-- Filtros off-road
CREATE INDEX idx_suspension_clearance ON suspension_specs (ground_clearance_mm);
CREATE INDEX idx_suspension_approach  ON suspension_specs (approach_angle_deg);

-- Filtros de capacidade
CREATE INDEX idx_dimensions_towing    ON dimensions (towing_capacity_kg);
CREATE INDEX idx_dimensions_payload   ON dimensions (payload_kg);

-- Features por categoria
CREATE INDEX idx_features_category    ON features (category);
CREATE INDEX idx_vf_feature_id        ON vehicle_features (feature_id);

-- ============================================================
-- SEED DATA — Brands
-- ============================================================

INSERT INTO brands (name, country) VALUES
    ('Ford',        'Estados Unidos'),
    ('Chevrolet',   'Estados Unidos'),
    ('GMC',         'Estados Unidos'),
    ('Toyota',      'Japão'),
    ('Jeep',        'Estados Unidos'),
    ('Volkswagen',  'Alemanha'),
    ('Nissan',      'Japão'),
    ('Mitsubishi',  'Japão'),
    ('BYD',         'China'),
    ('RAM',         'Estados Unidos');

-- ============================================================
-- SEED DATA — Vehicles
-- ============================================================

INSERT INTO vehicles (brand_id, model, version, model_year, fuel_type, category, is_reference) VALUES
    -- Referência
    ((SELECT id FROM brands WHERE name = 'Ford'),
     'Ranger', 'Raptor V6 EcoBoost Twin-Turbo', 2024, 'gasolina', 'desert_runner', TRUE),

    -- Desert Runners
    ((SELECT id FROM brands WHERE name = 'Chevrolet'),
     'Colorado', 'ZR2 Bison', 2025, 'gasolina', 'desert_runner', FALSE),
    ((SELECT id FROM brands WHERE name = 'GMC'),
     'Canyon', 'AT4X AEV Edition', 2024, 'gasolina', 'desert_runner', FALSE),
    ((SELECT id FROM brands WHERE name = 'Toyota'),
     'Tacoma', 'TRD Pro Hybrid', 2024, 'hibrido', 'desert_runner', FALSE),

    -- Rock Crawler
    ((SELECT id FROM brands WHERE name = 'Jeep'),
     'Gladiator', 'Rubicon', 2025, 'gasolina', 'rock_crawler', FALSE),

    -- Diesel Global
    ((SELECT id FROM brands WHERE name = 'Toyota'),
     'Hilux', 'GR-Sport', 2024, 'diesel', 'diesel_global', FALSE),
    ((SELECT id FROM brands WHERE name = 'Volkswagen'),
     'Amarok', 'V6 Extreme', 2024, 'diesel', 'diesel_global', FALSE),
    ((SELECT id FROM brands WHERE name = 'Nissan'),
     'Frontier', 'PRO-4X', 2024, 'diesel', 'diesel_global', FALSE),
    ((SELECT id FROM brands WHERE name = 'Mitsubishi'),
     'L200 Triton', 'Sport Outdoor Plus', 2025, 'diesel', 'diesel_global', FALSE),

    -- Hybrid Disruptor
    ((SELECT id FROM brands WHERE name = 'BYD'),
     'Shark', 'DMO PHEV', 2025, 'phev', 'hybrid_disruptor', FALSE),

    -- Full Size
    ((SELECT id FROM brands WHERE name = 'RAM'),
     '1500', 'Rebel HEMI V8', 2024, 'gasolina', 'full_size', FALSE);

-- ============================================================
-- SEED DATA — Engine Specs
-- ============================================================

INSERT INTO engine_specs (
    vehicle_id, engine_type, displacement_liters, cylinders, cylinder_layout,
    turbo_type, power_hp, torque_nm, torque_rpm,
    acceleration_0_100_s, top_speed_kmh,
    fuel_consumption_urban_kml, fuel_consumption_hwy_kml,
    tank_capacity_liters, estimated_range_km
) VALUES

-- Ford Ranger Raptor
((SELECT id FROM vehicles WHERE model = 'Ranger'),
 '3.0L V6 EcoBoost Twin-Turbo', 3.0, 6, 'V',
 'twin-turbo', 405, 583, NULL,
 5.8, NULL, NULL, NULL, NULL, NULL),

-- Chevrolet Colorado ZR2 Bison
((SELECT id FROM vehicles WHERE model = 'Colorado'),
 '2.7L TurboMax I-4', 2.7, 4, 'em linha',
 'turbo', 310, 583, 3000,
 6.8, NULL, NULL, NULL, NULL, NULL),

-- GMC Canyon AT4X
((SELECT id FROM vehicles WHERE model = 'Canyon'),
 '2.7L TurboMax I-4', 2.7, 4, 'em linha',
 'turbo', 310, 583, 3000,
 7.4, NULL, NULL, NULL, NULL, NULL),

-- Toyota Tacoma TRD Pro
((SELECT id FROM vehicles WHERE model = 'Tacoma'),
 'i-FORCE MAX Hybrid 2.4T', 2.4, 4, 'em linha',
 'turbo', 326, 630, NULL,
 NULL, NULL, NULL, NULL, NULL, NULL),

-- Jeep Gladiator Rubicon
((SELECT id FROM vehicles WHERE model = 'Gladiator'),
 '3.6L V6 Pentastar', 3.6, 6, 'V',
 'aspirado', 285, 352, 4400,
 8.7, NULL, NULL, NULL, 83, NULL),

-- Toyota Hilux GR-Sport
((SELECT id FROM vehicles WHERE model = 'Hilux'),
 '2.8L 16V Turbo Intercooler', 2.8, 4, 'em linha',
 'geometria variável', 224, 550, 2800,
 NULL, 180, 10.5, 9.3, 80, 856),

-- VW Amarok V6 Extreme
((SELECT id FROM vehicles WHERE model = 'Amarok'),
 '3.0L V6 TDI', 3.0, 6, 'V',
 'geometria variável', 258, 580, 1750,
 8.3, 191, 11.9, 9.9, 80, 900),

-- Nissan Frontier PRO-4X
((SELECT id FROM vehicles WHERE model = 'Frontier'),
 'YS23 2.3L Biturbo Diesel', 2.3, 4, 'em linha',
 'biturbo', 190, 450, 1500,
 11.3, 180, 11.0, 9.1, 73, 803),

-- Mitsubishi L200 Triton
((SELECT id FROM vehicles WHERE model = 'L200 Triton'),
 'MIVEC 2.4L Diesel', 2.4, 4, 'em linha',
 'geometria variável', 190, 430, NULL,
 10.4, NULL, NULL, NULL, 75, NULL),

-- BYD Shark DMO
((SELECT id FROM vehicles WHERE model = 'Shark'),
 '1.5L Turbo + Dual Motor Elétrico (PHEV)', 1.5, 4, 'em linha',
 'turbo', 437, NULL, 0,
 5.7, 250, NULL, NULL, NULL, NULL),

-- RAM 1500 Rebel
((SELECT id FROM vehicles WHERE model = '1500'),
 '5.7L HEMI V8', 5.7, 8, 'V',
 'aspirado', 400, 556, 3950,
 6.4, 174, 18.9, 15.2, 98, NULL);

-- ============================================================
-- SEED DATA — Drivetrain Specs
-- ============================================================

INSERT INTO drivetrain_specs (
    vehicle_id, transmission_type, gears, traction_type,
    has_low_range, diff_lock_type, has_hill_descent, has_hill_start, drive_modes
) VALUES

((SELECT id FROM vehicles WHERE model = 'Ranger'),
 'automatica', 10, '4x4_part_time',
 TRUE, 'eletrônico traseiro', TRUE, TRUE,
 '["Normal", "Sport", "Slippery", "Rock", "Sand", "Mud", "Baja"]'),

((SELECT id FROM vehicles WHERE model = 'Colorado'),
 'automatica', 8, '4x4_part_time',
 TRUE, 'eletrônico dianteiro e traseiro', TRUE, TRUE,
 '["Normal", "Terrain", "Baja"]'),

((SELECT id FROM vehicles WHERE model = 'Canyon'),
 'automatica', 8, '4x4_part_time',
 TRUE, 'eletrônico dianteiro e traseiro', TRUE, TRUE,
 '["Normal", "Tow/Haul", "Off-Road", "Terrain", "Baja"]'),

((SELECT id FROM vehicles WHERE model = 'Tacoma'),
 'automatica', 8, '4x4_part_time',
 TRUE, 'eletrônico traseiro', TRUE, TRUE,
 '["Normal", "Eco", "Sport", "Trail", "Rock", "Dirt", "Sand", "Snow"]'),

((SELECT id FROM vehicles WHERE model = 'Gladiator'),
 'manual', 6, '4x4_part_time',
 TRUE, 'Tru-Lok eletrônico dianteiro e traseiro', TRUE, TRUE,
 '["Off-Road+"]'),

((SELECT id FROM vehicles WHERE model = 'Hilux'),
 'automatica', 6, '4x4_part_time',
 TRUE, 'A-TRC eletrônico', TRUE, TRUE,
 '["Eco", "Power"]'),

((SELECT id FROM vehicles WHERE model = 'Amarok'),
 'automatica', 10, '4x4_full_time',
 TRUE, 'diferencial traseiro blocante', TRUE, TRUE,
 '["Areia", "Neve", "Cascalho"]'),

((SELECT id FROM vehicles WHERE model = 'Frontier'),
 'automatica', 7, '4x4_part_time',
 TRUE, 'eletrônico traseiro', TRUE, TRUE,
 '["Standard", "Sport", "Off-Road", "Tow"]'),

((SELECT id FROM vehicles WHERE model = 'L200 Triton'),
 'automatica', 6, '4x4_part_time',
 TRUE, 'Torsen central + eletrônico traseiro', TRUE, TRUE,
 '["Cascalho", "Lama/Neve", "Areia", "Pedra"]'),

((SELECT id FROM vehicles WHERE model = 'Shark'),
 'e_cvt', NULL, 'e_awd',
 FALSE, 'vetorização elétrica por roda', TRUE, TRUE,
 '["HEV", "EV", "Neve", "Areia", "Lama"]'),

((SELECT id FROM vehicles WHERE model = '1500'),
 'automatica', 8, '4x4_part_time',
 TRUE, 'eletrônico integrado', TRUE, TRUE,
 '["2H", "4H", "4L"]');

-- ============================================================
-- SEED DATA — Suspension Specs
-- ============================================================

INSERT INTO suspension_specs (
    vehicle_id, front_suspension, rear_suspension, front_shock_brand,
    ground_clearance_mm, approach_angle_deg, departure_angle_deg,
    water_crossing_mm, tire_type, tire_size_inches, tire_spec, offroad_profile
) VALUES

((SELECT id FROM vehicles WHERE model = 'Ranger'),
 'Independente com amortecedores Fox Live Valve (controle eletrônico)',
 'Eixo rígido com amortecedores Fox Live Valve', 'Fox',
 283, 32.5, 24.0, 850, 'All-Terrain', 33, '285/70 R17', 'Desert Running'),

((SELECT id FROM vehicles WHERE model = 'Colorado'),
 'Independente com amortecedores Multimatic DSSV',
 'Eixo rígido com amortecedores Multimatic DSSV', 'Multimatic',
 308, 38.0, 26.0, NULL, 'All-Terrain', 35, '35"', 'Desert Running / Rock Crawling'),

((SELECT id FROM vehicles WHERE model = 'Canyon'),
 'Braços de controle com amortecedores Multimatic DSSV',
 'Live axle com amortecedores Multimatic DSSV', 'Multimatic',
 272, 38.2, 26.0, NULL, 'Mud-Terrain Goodyear', 35, '35"', 'Desert Running / Rock Crawling'),

((SELECT id FROM vehicles WHERE model = 'Tacoma'),
 'Independente, curso de 9,6 polegadas',
 'Multi-link com molas helicoidais, curso de 10,2 polegadas', NULL,
 NULL, NULL, NULL, NULL, 'All-Terrain', 33, '33"', 'Baja / Trail'),

((SELECT id FROM vehicles WHERE model = 'Gladiator'),
 'Eixo rígido com molas helicoidais e amortecedores Fox/Heavy Duty',
 'Eixo rígido com braços oscilantes e molas helicoidais', 'Fox',
 294, 43.4, 26.0, 762, 'Mud-Terrain', 17, '285/70 R17', 'Rock Crawling'),

((SELECT id FROM vehicles WHERE model = 'Hilux'),
 'Independente com reforços estruturais',
 'Eixo rígido com feixe de molas recalibrado', NULL,
 323, 29.0, 26.0, 700, 'All-Terrain', 18, '265/60 R18', 'Rally / Dakar'),

((SELECT id FROM vehicles WHERE model = 'Amarok'),
 'Independente elevada +30mm',
 'Multilink traseira', NULL,
 234, 29.0, 21.0, 800, 'All-Terrain', 18, '265/60 R18', 'Rodoviário / Off-Road'),

((SELECT id FROM vehicles WHERE model = 'Frontier'),
 'Duplo braço oscilante',
 'Five-link com molas helicoidais', NULL,
 251, 31.6, 25.7, 600, 'All-Terrain', 17, 'Aro 17 PRO-4X', 'Off-Road equilibrado'),

((SELECT id FROM vehicles WHERE model = 'L200 Triton'),
 'Duplo braço A independente',
 'Eixo rígido com feixe de molas', NULL,
 220, 32.0, 23.0, 600, 'All-Terrain misto', 18, 'Aro 18', 'Carga pesada / Off-Road'),

((SELECT id FROM vehicles WHERE model = 'Shark'),
 'Duplo A independente integrado ao chassi DMO',
 'Independente multi-link', NULL,
 NULL, NULL, NULL, NULL, 'Misto de alto arrasto', NULL, NULL, 'Vetorização elétrica'),

((SELECT id FROM vehicles WHERE model = '1500'),
 'Duplo A com amortecedores Bilstein',
 'Five-link com track bar e amortecedores Bilstein', 'Bilstein',
 249, 25.1, 22.6, 800, 'All-Terrain', 18, '275/65 R18', 'Rodoviário / Desert');

-- ============================================================
-- SEED DATA — Dimensions
-- ============================================================

INSERT INTO dimensions (
    vehicle_id, length_mm, width_mm, height_mm, wheelbase_mm,
    curb_weight_kg, payload_kg, towing_capacity_kg,
    bed_volume_liters, passenger_capacity
) VALUES

((SELECT id FROM vehicles WHERE model = 'Ranger'),
 5363, 2028, 1910, 3270, 2100, NULL, 2494, NULL, 5),

((SELECT id FROM vehicles WHERE model = 'Colorado'),
 5402, 2143, 2077, 3337, 2234, NULL, 2721, NULL, 5),

((SELECT id FROM vehicles WHERE model = 'Canyon'),
 NULL, NULL, NULL, 3337, NULL, NULL, 2721, NULL, 5),

((SELECT id FROM vehicles WHERE model = 'Tacoma'),
 NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 5),

((SELECT id FROM vehicles WHERE model = 'Gladiator'),
 5591, 1894, 1905, 3488, 2104, 674, 1814, NULL, 5),

((SELECT id FROM vehicles WHERE model = 'Hilux'),
 5325, 1900, 1820, 3085, 2270, 1000, 3500, NULL, 5),

((SELECT id FROM vehicles WHERE model = 'Amarok'),
 5350, 1944, 1834, 3270, 2438, 1005, 3500, 1280, 5),

((SELECT id FROM vehicles WHERE model = 'Frontier'),
 5260, 1850, 1860, 3150, 2100, 1030, 2750, 1054, 5),

((SELECT id FROM vehicles WHERE model = 'L200 Triton'),
 5300, 1820, 1795, 3000, 1950, 1000, 3500, NULL, 5),

((SELECT id FROM vehicles WHERE model = 'Shark'),
 4950, 1980, 1650, 2950, NULL, NULL, NULL, NULL, 5),

((SELECT id FROM vehicles WHERE model = '1500'),
 5929, 2084, 2012, 3672, 2610, 610, 4935, 1200, 5);

-- ============================================================
-- SEED DATA — Warranty
-- ============================================================

INSERT INTO warranty (
    vehicle_id, warranty_years, powertrain_warranty_years,
    battery_warranty_years, service_interval_km,
    avg_service_cost_brl, has_roadside_assistance, warranty_notes
) VALUES

((SELECT id FROM vehicles WHERE model = 'Ranger'),
 3, 5, NULL, 10000, NULL, TRUE, NULL),

((SELECT id FROM vehicles WHERE model = 'Colorado'),
 NULL, NULL, NULL, NULL, NULL, NULL, NULL),

((SELECT id FROM vehicles WHERE model = 'Canyon'),
 NULL, NULL, NULL, NULL, NULL, TRUE, NULL),

((SELECT id FROM vehicles WHERE model = 'Tacoma'),
 3, 5, 8, NULL, NULL, TRUE, 'ToyotaCare gratuito 2 anos/40.000 km'),

((SELECT id FROM vehicles WHERE model = 'Gladiator'),
 NULL, NULL, NULL, NULL, NULL, NULL, NULL),

((SELECT id FROM vehicles WHERE model = 'Hilux'),
 5, 5, NULL, 10000, NULL, TRUE, 'Revisões preço fixo Toyota'),

((SELECT id FROM vehicles WHERE model = 'Amarok'),
 5, 5, NULL, 10000, 1768.50, TRUE, 'VW Service 24h (12 meses iniciais)'),

((SELECT id FROM vehicles WHERE model = 'Frontier'),
 6, 6, NULL, 10000, NULL, TRUE, 'Nissan Way Assistance'),

((SELECT id FROM vehicles WHERE model = 'L200 Triton'),
 5, 5, NULL, 10000, NULL, TRUE, 'MIT Assistance — sem limite de km para pessoa física'),

((SELECT id FROM vehicles WHERE model = 'Shark'),
 6, 8, 8, NULL, NULL, NULL, '6 anos sem limite de km — bateria Blade 8 anos'),

((SELECT id FROM vehicles WHERE model = '1500'),
 3, 3, NULL, NULL, NULL, TRUE, 'RAM Flexcare preço fixo');

-- ============================================================
-- SEED DATA — Features (catálogo)
-- ============================================================

INSERT INTO features (name, category) VALUES
    -- Conforto
    ('Bancos em couro',                     'conforto'),
    ('Aquecimento dos bancos',              'conforto'),
    ('Ventilação dos bancos',               'conforto'),
    ('Banco do motorista com memória',      'conforto'),
    ('Ar digital dual zone',                'conforto'),
    ('Saída de ar traseira',                'conforto'),
    ('Partida por botão',                   'conforto'),
    ('Freio de estacionamento eletrônico',  'conforto'),
    ('Carregador wireless',                 'conforto'),

    -- Conectividade
    ('Apple CarPlay',                       'conectividade'),
    ('Android Auto',                        'conectividade'),
    ('Conexão sem fio para smartphone',     'conectividade'),
    ('Wi-Fi embarcado',                     'conectividade'),
    ('Partida remota',                      'conectividade'),
    ('Monitoramento remoto do veículo',     'conectividade'),
    ('Atualização remota OTA',              'conectividade'),
    ('Aplicativo de conectividade',         'conectividade'),

    -- Segurança
    ('Câmera de ré',                        'seguranca'),
    ('Câmera 360°',                         'seguranca'),
    ('Frenagem automática de emergência',   'seguranca'),
    ('Alerta de ponto cego',                'seguranca'),
    ('Piloto automático adaptativo',        'seguranca'),
    ('Assistente de permanência em faixa',  'seguranca'),
    ('Monitoramento de pressão dos pneus',  'seguranca'),
    ('ISOFIX',                              'seguranca'),
    ('Controle de estabilidade',            'seguranca'),

    -- Tecnologia
    ('Sistema de som premium',              'tecnologia'),
    ('Painel de instrumentos digital',      'tecnologia'),
    ('Navegação embarcada',                 'tecnologia'),
    ('Head-up display (HUD)',               'tecnologia'),
    ('Assistente de reboque',               'tecnologia'),
    ('Câmeras underbody',                   'tecnologia'),
    ('Launch Control',                      'tecnologia'),
    ('Tomada 220V / V2L',                   'tecnologia'),

    -- Exterior
    ('Farol LED',                           'exterior'),
    ('Estribo / Rock sliders',              'exterior'),
    ('Rack de teto',                        'exterior'),
    ('Capota marítima',                     'exterior'),
    ('Protetor de caçamba',                 'exterior');

-- ============================================================
-- SEED DATA — Vehicle Features (amostra)
-- ============================================================

-- Ford Ranger Raptor
INSERT INTO vehicle_features (vehicle_id, feature_id, value)
SELECT
    (SELECT id FROM vehicles WHERE model = 'Ranger'),
    id, v.val
FROM features, (VALUES
    ('Apple CarPlay',                       'Sim (sem fio)'),
    ('Android Auto',                        'Sim (sem fio)'),
    ('Câmera 360°',                         'Sim'),
    ('Frenagem automática de emergência',   'Sim'),
    ('Alerta de ponto cego',                'Sim'),
    ('Piloto automático adaptativo',        'Sim'),
    ('Farol LED',                           'Sim'),
    ('Partida por botão',                   'Sim'),
    ('Ar digital dual zone',                'Sim'),
    ('Assistente de reboque',               'Sim'),
    ('Tomada 220V / V2L',                   'Não'),
    ('Atualização remota OTA',              'Sim'),
    ('Launch Control',                      'Sim (modo Baja)')
) AS v(name, val)
WHERE features.name = v.name;

-- BYD Shark
INSERT INTO vehicle_features (vehicle_id, feature_id, value)
SELECT
    (SELECT id FROM vehicles WHERE model = 'Shark'),
    id, v.val
FROM features, (VALUES
    ('Apple CarPlay',                       'Sim'),
    ('Android Auto',                        'Sim'),
    ('Câmera 360°',                         'Sim'),
    ('Carregador wireless',                 'Sim (50W)'),
    ('Tomada 220V / V2L',                   'Sim (V2L — alimenta equipamentos externos)'),
    ('Atualização remota OTA',              'Sim'),
    ('Head-up display (HUD)',               'Sim'),
    ('Monitoramento de pressão dos pneus',  'Sim (TPMS direto)'),
    ('Frenagem automática de emergência',   'Sim'),
    ('Wi-Fi embarcado',                     'Sim (4G embarcado)'),
    ('Partida por botão',                   'Sim'),
    ('Freio de estacionamento eletrônico',  'Sim')
) AS v(name, val)
WHERE features.name = v.name;

-- VW Amarok
INSERT INTO vehicle_features (vehicle_id, feature_id, value)
SELECT
    (SELECT id FROM vehicles WHERE model = 'Amarok'),
    id, v.val
FROM features, (VALUES
    ('Bancos em couro',                     'Sim (Couro Nappa)'),
    ('Aquecimento dos bancos',              'Sim'),
    ('Ventilação dos bancos',               'Sim'),
    ('Apple CarPlay',                       'Sim (sem fio)'),
    ('Android Auto',                        'Sim'),
    ('Carregador wireless',                 'Sim'),
    ('Farol LED',                           'Sim (Full-LED Matrix)'),
    ('Rack de teto',                        'Sim (capacidade 80 kg)'),
    ('Capota marítima',                     'Sim'),
    ('Câmera 360°',                         'Não'),
    ('Câmera de ré',                        'Sim'),
    ('Atualização remota OTA',              'Sim'),
    ('Assistente de reboque',               'Sim (Trailer Assist)'),
    ('Tomada 220V / V2L',                   'Não')
) AS v(name, val)
WHERE features.name = v.name;

-- ============================================================
-- VIEWS ÚTEIS PARA A API
-- ============================================================

-- View principal: dados completos por veículo (para listagem e comparação)
CREATE VIEW vw_vehicle_full AS
SELECT
    v.id,
    b.name                          AS brand,
    v.model,
    v.version,
    v.model_year,
    v.fuel_type,
    v.category,
    v.is_reference,
    v.image_url,

    -- Engine
    e.engine_type,
    e.displacement_liters,
    e.cylinders,
    e.power_hp,
    e.torque_nm,
    e.acceleration_0_100_s,
    e.top_speed_kmh,
    e.fuel_consumption_urban_kml,
    e.fuel_consumption_hwy_kml,
    e.tank_capacity_liters,
    e.estimated_range_km,

    -- Drivetrain
    d.transmission_type,
    d.gears,
    d.traction_type,
    d.has_low_range,
    d.diff_lock_type,
    d.drive_modes,

    -- Suspension
    s.front_suspension,
    s.rear_suspension,
    s.front_shock_brand,
    s.ground_clearance_mm,
    s.approach_angle_deg,
    s.departure_angle_deg,
    s.water_crossing_mm,
    s.tire_size_inches,
    s.tire_spec,
    s.offroad_profile,

    -- Dimensions
    dim.length_mm,
    dim.width_mm,
    dim.height_mm,
    dim.wheelbase_mm,
    dim.curb_weight_kg,
    dim.payload_kg,
    dim.towing_capacity_kg,
    dim.bed_volume_liters,

    -- Warranty
    w.warranty_years,
    w.powertrain_warranty_years,
    w.service_interval_km,
    w.avg_service_cost_brl

FROM vehicles v
JOIN brands            b   ON b.id  = v.brand_id
LEFT JOIN engine_specs     e   ON e.vehicle_id   = v.id
LEFT JOIN drivetrain_specs d   ON d.vehicle_id   = v.id
LEFT JOIN suspension_specs s   ON s.vehicle_id   = v.id
LEFT JOIN dimensions       dim ON dim.vehicle_id = v.id
LEFT JOIN warranty         w   ON w.vehicle_id   = v.id;

-- View de features por veículo (JSON agregado)
CREATE VIEW vw_vehicle_features AS
SELECT
    v.id AS vehicle_id,
    b.name AS brand,
    v.model,
    v.version,
    JSONB_OBJECT_AGG(f.name, vf.value) AS features
FROM vehicles v
JOIN brands b ON b.id = v.brand_id
JOIN vehicle_features vf ON vf.vehicle_id = v.id
JOIN features f ON f.id = vf.feature_id
GROUP BY v.id, b.name, v.model, v.version;

-- ============================================================
-- SEED DATA — Spec Category Map (dirige o comparativo dinâmico)
-- ============================================================

INSERT INTO spec_category_map
    (category_key, category_label, field_source, field_name, field_label, display_unit, display_order, radar_weight)
VALUES
    -- Motor
    ('motor',       'Motor',      'engine_specs',      'engine_type',           'Tipo de motor',                NULL,      1,  1.00),
    ('motor',       'Motor',      'engine_specs',      'displacement_liters',   'Cilindrada',                   'L',       2,  1.00),
    ('motor',       'Motor',      'engine_specs',      'cylinders',             'Cilindros',                    NULL,      3,  1.00),
    ('motor',       'Motor',      'engine_specs',      'turbo_type',            'Turbo',                        NULL,      4,  1.00),
    ('motor',       'Motor',      'engine_specs',      'power_hp',              'Potência',                     'cv',      5,  1.00),
    ('motor',       'Motor',      'engine_specs',      'torque_nm',             'Torque',                       'Nm',      6,  1.00),

    -- Desempenho
    ('desempenho',  'Desempenho', 'engine_specs',      'acceleration_0_100_s',  'Aceleração 0-100 km/h',        's',       1,  1.00),
    ('desempenho',  'Desempenho', 'engine_specs',      'top_speed_kmh',         'Velocidade máxima',            'km/h',    2,  1.00),
    ('desempenho',  'Desempenho', 'engine_specs',      'fuel_consumption_urban_kml', 'Consumo urbano',       'km/l',    3,  1.00),
    ('desempenho',  'Desempenho', 'engine_specs',      'estimated_range_km',    'Autonomia estimada',           'km',      4,  1.00),

    -- Off-road
    ('offroad',     'Off-road',   'suspension_specs',  'ground_clearance_mm',   'Altura livre do solo',         'mm',      1,  1.00),
    ('offroad',     'Off-road',   'suspension_specs',  'approach_angle_deg',    'Ângulo de entrada',            '°',       2,  1.00),
    ('offroad',     'Off-road',   'suspension_specs',  'departure_angle_deg',   'Ângulo de saída',              '°',       3,  1.00),
    ('offroad',     'Off-road',   'suspension_specs',  'water_crossing_mm',     'Imersão máxima',               'mm',      4,  1.00),
    ('offroad',     'Off-road',   'suspension_specs',  'front_shock_brand',     'Amortecedores',                NULL,      5,  1.00),
    ('offroad',     'Off-road',   'drivetrain_specs',  'diff_lock_type',        'Bloqueio do diferencial',      NULL,      6,  1.00),

    -- Capacidade
    ('capacidade',  'Capacidade', 'dimensions',        'payload_kg',            'Carga útil',                   'kg',      1,  1.00),
    ('capacidade',  'Capacidade', 'dimensions',        'towing_capacity_kg',    'Capacidade de reboque',        'kg',      2,  1.00),
    ('capacidade',  'Capacidade', 'dimensions',        'bed_volume_liters',     'Volume da caçamba',            'L',       3,  1.00),
    ('capacidade',  'Capacidade', 'warranty',          'warranty_years',        'Garantia',                     'anos',    4,  1.00),
    ('capacidade',  'Capacidade', 'warranty',          'avg_service_cost_brl',  'Custo médio de revisão',       'R$',      5,  1.00),

    -- Tecnologia
    ('tecnologia',  'Tecnologia', 'vehicle_features',  'Sistema de som premium',       'Sistema de som premium',    NULL,  1,  1.00),
    ('tecnologia',  'Tecnologia', 'vehicle_features',  'Painel de instrumentos digital', 'Painel de instrumentos digital', NULL, 2,  1.00),
    ('tecnologia',  'Tecnologia', 'vehicle_features',  'Navegação embarcada',          'Navegação embarcada',      NULL,  3,  1.00),
    ('tecnologia',  'Tecnologia', 'vehicle_features',  'Head-up display (HUD)',        'Head-up display (HUD)',    NULL,  4,  1.00),
    ('tecnologia',  'Tecnologia', 'vehicle_features',  'Câmeras underbody',            'Câmeras underbody',        NULL,  5,  1.00),
    ('tecnologia',  'Tecnologia', 'vehicle_features',  'Tomada 220V / V2L',            'Tomada 220V / V2L',        NULL,  6,  1.00),

    -- Conforto
    ('conforto',    'Conforto',   'vehicle_features',  'Bancos em couro',              'Bancos em couro',          NULL,  1,  1.00),
    ('conforto',    'Conforto',   'vehicle_features',  'Aquecimento dos bancos',       'Aquecimento dos bancos',   NULL,  2,  1.00),
    ('conforto',    'Conforto',   'vehicle_features',  'Ventilação dos bancos',        'Ventilação dos bancos',    NULL,  3,  1.00),
    ('conforto',    'Conforto',   'vehicle_features',  'Ar digital dual zone',         'Ar digital dual zone',     NULL,  4,  1.00),
    ('conforto',    'Conforto',   'vehicle_features',  'Partida por botão',            'Partida por botão',        NULL,  5,  1.00),
    ('conforto',    'Conforto',   'vehicle_features',  'Carregador wireless',          'Carregador wireless',      NULL,  6,  1.00),

    -- Segurança
    ('seguranca',   'Segurança',  'vehicle_features',  'Câmera 360°',                 'Câmera 360°',              NULL,  1,  1.00),
    ('seguranca',   'Segurança',  'vehicle_features',  'Frenagem automática de emergência', 'Frenagem automática de emergência', NULL, 2,  1.00),
    ('seguranca',   'Segurança',  'vehicle_features',  'Alerta de ponto cego',         'Alerta de ponto cego',     NULL,  3,  1.00),
    ('seguranca',   'Segurança',  'vehicle_features',  'Piloto automático adaptativo', 'Piloto automático adaptativo', NULL, 4,  1.00),
    ('seguranca',   'Segurança',  'vehicle_features',  'Monitoramento de pressão dos pneus', 'Monitoramento de pressão dos pneus', NULL, 5,  1.00),
    ('seguranca',   'Segurança',  'vehicle_features',  'Farol LED',                    'Farol LED',                NULL,  6,  1.00),
    ('seguranca',   'Segurança',  'vehicle_features',  'Controle de estabilidade',     'Controle de estabilidade', NULL,  7,  1.00);

-- ============================================================
-- PREENCHIMENTO DE CAMPOS DE DESEMPENHO AINDA NULL (demonstrativo)
-- ============================================================

UPDATE engine_specs SET top_speed_kmh = 180, fuel_consumption_urban_kml = 7.9,
    fuel_consumption_hwy_kml = 9.5, tank_capacity_liters = 80, estimated_range_km = 642
WHERE vehicle_id = (SELECT id FROM vehicles WHERE model = 'Ranger');

UPDATE engine_specs SET top_speed_kmh = 180, fuel_consumption_urban_kml = 8.5,
    fuel_consumption_hwy_kml = 10.3, tank_capacity_liters = 70, estimated_range_km = 648
WHERE vehicle_id = (SELECT id FROM vehicles WHERE model = 'Colorado');

UPDATE engine_specs SET top_speed_kmh = 178, fuel_consumption_urban_kml = 8.4,
    fuel_consumption_hwy_kml = 10.0, tank_capacity_liters = 70, estimated_range_km = 640
WHERE vehicle_id = (SELECT id FROM vehicles WHERE model = 'Canyon');

UPDATE engine_specs SET top_speed_kmh = 170, fuel_consumption_urban_kml = 9.3,
    fuel_consumption_hwy_kml = 10.7, tank_capacity_liters = 80, estimated_range_km = 740
WHERE vehicle_id = (SELECT id FROM vehicles WHERE model = 'Tacoma');

UPDATE engine_specs SET top_speed_kmh = 177, fuel_consumption_urban_kml = 7.0,
    fuel_consumption_hwy_kml = 8.5, estimated_range_km = 500
WHERE vehicle_id = (SELECT id FROM vehicles WHERE model = 'Gladiator');

UPDATE engine_specs SET top_speed_kmh = 175, fuel_consumption_urban_kml = 10.2,
    fuel_consumption_hwy_kml = 12.0, estimated_range_km = 780
WHERE vehicle_id = (SELECT id FROM vehicles WHERE model = 'L200 Triton');

UPDATE engine_specs SET fuel_consumption_urban_kml = 20.5,
    fuel_consumption_hwy_kml = 12.0, tank_capacity_liters = 60, estimated_range_km = 840
WHERE vehicle_id = (SELECT id FROM vehicles WHERE model = 'Shark');

UPDATE engine_specs SET estimated_range_km = 700
WHERE vehicle_id = (SELECT id FROM vehicles WHERE model = '1500');

-- ============================================================
-- SEED DATA — Customer Profiles (perfil do cliente)
-- ============================================================

CREATE TABLE customer_profiles (
    type        VARCHAR(30) PRIMARY KEY,
    label       VARCHAR(60)  NOT NULL,
    description TEXT         NOT NULL
);

CREATE TABLE profile_detection_keywords (
    id           SERIAL PRIMARY KEY,
    profile_type VARCHAR(30) NOT NULL REFERENCES customer_profiles(type) ON DELETE CASCADE,
    keyword      VARCHAR(60) NOT NULL,
    weight       INT NOT NULL DEFAULT 1
);

CREATE TABLE profile_signals (
    id            SERIAL PRIMARY KEY,
    profile_type  VARCHAR(30) NOT NULL REFERENCES customer_profiles(type) ON DELETE CASCADE,
    signal        VARCHAR(180) NOT NULL,
    display_order INT NOT NULL DEFAULT 0
);

CREATE TABLE sales_arguments (
    id            SERIAL PRIMARY KEY,
    profile_type  VARCHAR(30) NOT NULL REFERENCES customer_profiles(type) ON DELETE CASCADE,
    title         VARCHAR(120) NOT NULL,
    description   TEXT NOT NULL,
    urgency       VARCHAR(10) NOT NULL CHECK (urgency IN ('high','medium','low')),
    display_order INT NOT NULL DEFAULT 0
);

INSERT INTO customer_profiles (type, label, description) VALUES
('enthusiast', 'Entusiasta Off-Road', 'Busca a picape de maior desempenho da categoria. Valoriza potência, capacidade fora-de-estrada e itens exclusivos — e está disposto a pagar por isso.'),
('lifestyle',  'Lifestyle & Conforto', 'Usa o veículo no dia a dia e em viagens com a família. Prioriza acabamento, conforto, design e tecnologia de conveniência.'),
('rational',   'Racional (Custo-Benefício)', 'Comparou consumo, autonomia e custo de manutenção antes de chegar à decisão. Precisa de justificativa objetiva de economia e robustez.'),
('tech',       'Entusiasta de Tecnologia', 'Se interessa por conectividade, assistentes de condução e recursos inteligentes. Modernidade e atualizações pesam na escolha.');

INSERT INTO profile_detection_keywords (profile_type, keyword, weight) VALUES
('enthusiast', 'ranger', 3),
('enthusiast', 'raptor', 3),
('enthusiast', 'performance', 2),
('enthusiast', 'potencia', 2),
('enthusiast', 'offroad', 2),
('enthusiast', 'baja', 2),
('enthusiast', 'turbo', 1),
('enthusiast', 'launch', 1),
('enthusiast', 'supertransmissao', 1),
('lifestyle', 'conforto', 2),
('lifestyle', 'design', 2),
('lifestyle', 'couro', 2),
('lifestyle', 'multimidia', 2),
('lifestyle', '1500', 3),
('lifestyle', 'gladiator', 2),
('lifestyle', 'canyon', 2),
('lifestyle', 'som', 1),
('lifestyle', 'ventilacao', 1),
('rational', 'consumo', 3),
('rational', 'economia', 3),
('rational', 'diesel', 2),
('rational', 'autonomia', 2),
('rational', 'manutencao', 2),
('rational', 'hilux', 3),
('rational', 'amarok', 2),
('rational', 'frontier', 2),
('rational', 'l200', 2),
('rational', 'reboque', 1),
('tech', 'tecnologia', 3),
('tech', 'hud', 2),
('tech', 'ota', 2),
('tech', 'sensores', 2),
('tech', 'shark', 3),
('tech', 'tacoma', 2),
('tech', 'multimidia 12', 2),
('tech', 'piloto', 1),
('tech', 'camara', 1);

INSERT INTO profile_signals (profile_type, signal, display_order) VALUES
('enthusiast', 'Pesquisou por potência e desempenho acima da média (405 cv na Ranger Raptor)', 1),
('enthusiast', 'Considerou itens exclusivos como suspensão FOX e Launch Control', 2),
('enthusiast', 'Comparou contra rivais de apelo esportivo antes de concluir', 3),
('lifestyle', 'Dá peso ao acabamento em couro, conforto e som premium', 1),
('lifestyle', 'Prioriza espaço para família e viagens de fim de semana', 2),
('lifestyle', 'Valoriza design e presença do veículo', 3),
('rational', 'Analisou consumo, autonomia e custo de manutenção', 1),
('rational', 'Quer justificativa objetiva de economia no uso diário', 2),
('rational', 'Comparou preço de entrada e reconhecimento do mercado', 3),
('tech', 'Explorou recursos de conectividade e assistentes de condução', 1),
('tech', 'Interessado em atualização OTA e equipamentos inovadores', 2),
('tech', 'Valoriza cameras, sensores e multimidia integrada', 3);

INSERT INTO sales_arguments (profile_type, title, description, urgency, display_order) VALUES
('enthusiast', 'Desempenho de referência', '0-100 km/h em 5,8 s com o motor V6 3.0 EcoBoost e transmissão de 10 marchas — a mais rápida da categoria.', 'high', 1),
('enthusiast', 'Suspensão FOX com tecnologia exclusiva', 'Amortecedores sem costura, modo Baja e Launch Control de série — equipamento que rivaliza com preparadoras.', 'medium', 2),
('enthusiast', 'Capacidade real de uso', 'Reboque de 2,5 t, imersão de 850 mm e modos 4x4 dedicados: argumentos técnicos que fecham a venda.', 'medium', 3),
('lifestyle', 'Conforto de SUV no segmento de picapes', 'Cabine com acabamento em couro, ar-condicionado digital e isolamento acústico superior.', 'high', 1),
('lifestyle', 'Tecnologia de conveniência', 'Câmera 360°, carregador sem fio e central multimídia grande com Android Auto/Apple CarPlay sem fio.', 'medium', 2),
('lifestyle', 'Segurança para a família', 'Frenagem de emergência, alerta de ponto cego e piloto automático adaptativo de série.', 'medium', 3),
('rational', 'Economia comprovada', 'Consumo urbano 7,9 km/l e 80 L de tanque — reduz o custo por quilômetro no uso diário.', 'high', 1),
('rational', 'Menos paradas, mais autonomia', 'Até 642 km de autonomia estimada: menos visitas ao posto e mais previsibilidade no orçamento.', 'medium', 2),
('rational', 'Menor custo de propriedade', 'Garantia de 3 anos e rede credenciada em todo o país, com valor de revenda consolidado no segmento.', 'low', 3),
('tech', 'Conectividade total', 'Atualização remota OTA, Wi-Fi embarcado e app de controle do veículo.', 'high', 1),
('tech', 'Assistentes de condução', 'Piloto automático adaptativo, frenagem de emergência e monitoramento de ponto cego integrados.', 'medium', 2),
('tech', 'Equipamentos que impressionam', 'Head-up display, câmera 360° e tomada 220 V/V2L para alimentar equipamentos no acampamento.', 'medium', 3);

-- ============================================================
-- EXEMPLO DE QUERIES QUE A API VAI USAR
-- ============================================================

-- 1. Comparação de performance: todos os carros ordenados por potência
-- SELECT brand, model, version, power_hp, torque_nm, acceleration_0_100_s
-- FROM vw_vehicle_full
-- ORDER BY power_hp DESC;

-- 2. Filtro off-road: carros com clearance > 280mm e ângulo de entrada > 30°
-- SELECT brand, model, version, ground_clearance_mm, approach_angle_deg
-- FROM vw_vehicle_full
-- WHERE ground_clearance_mm > 280 AND approach_angle_deg > 30;

-- 3. Comparação entre dois veículos específicos
-- SELECT * FROM vw_vehicle_full
-- WHERE id IN (1, 2);

-- 4. Features de um veículo específico
-- SELECT features FROM vw_vehicle_features WHERE vehicle_id = 1;

-- 5. Veículos por categoria
-- SELECT brand, model, version FROM vw_vehicle_full
-- WHERE category = 'diesel_global'
-- ORDER BY torque_nm DESC;
