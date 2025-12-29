-- Script de inicialización de datos para la tabla client
-- Este script se ejecuta automáticamente en modo dev al iniciar Quarkus

-- Insertar clientes de prueba
INSERT INTO client (id, name, document_id, email, status, created_at, updated_at) VALUES
(1, 'Juan Pérez', '12345678', 'juan.perez@email.com', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'María García', '87654321', 'maria.garcia@email.com', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Carlos Rodríguez', '11223344', 'carlos.rodriguez@email.com', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Ana Martínez', '44332211', 'ana.martinez@email.com', 'INACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Luis Hernández', '55667788', 'luis.hernandez@email.com', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'Laura Sánchez', '88776655', 'laura.sanchez@email.com', 'BLOCKED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 'Pedro Gómez', '99887766', 'pedro.gomez@email.com', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, 'Sofia López', '66778899', 'sofia.lopez@email.com', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Reiniciar la secuencia del ID
ALTER SEQUENCE client_id_seq RESTART WITH 9;
