CREATE DATABASE ciudadlimpia;
USE ciudadlimpia;

INSERT INTO rol (nombre, descripcion) VALUES
  ('ROLE_ADMIN', 'Funcionario municipal que gestiona reportes'),
  ('ROLE_CIUDADANO', 'Ciudadano que crea y hace seguimiento de reportes');

INSERT INTO usuario (
    dni,
    nombre,
    apellido,
    email,
    password_hash,
    telefono,
    foto_perfil,
    activo,
    created_at,
    updated_at,
    direccion,
    distrito,
    rol_id
)
VALUES (
    '12345678',
    'Carlos',
    'Ramirez',
    'admin@ciudadlimpia.com',
    '$2a$10$zwo.1S71zo5sN.sVm0NaIuQUmUEnuTMMDmJ0eWKK9En3/EZobcXIW',
    '987654321',
    NULL,
    1,
    NOW(),
    NOW(),
    'Municipalidad de Lima',
    'Lima',
    1
);

INSERT INTO nivel (nombre, descripcion, puntos_minimos, icono) VALUES
  ('Principiante', 'Recién unido a la causa', 0, '🌱'),
  ('Reportero', 'Ya tienes varios reportes enviados', 100, '📋'),
  ('Guardián urbano', 'Defensor activo de tu ciudad', 300, '🛡️'),
  ('Héroe de la ciudad', 'Referente de la comunidad', 700, '🏆');
  
  
  INSERT INTO badge
(id, nombre, descripcion, icono, condicion_tipo, condicion_valor)
VALUES
(1,'Primer Reporte',
 'Realizó su primer reporte ciudadano',
 'badge_first_report.png',
 'REPORTES',
 1),

(2,'Ciudadano Activo',
 'Envió 10 reportes',
 'badge_active.png',
 'REPORTES',
 10),

(3,'Guardián del Barrio',
 'Acumuló 500 puntos',
 'badge_guardian.png',
 'PUNTOS',
 500),

(4,'Héroe Ambiental',
 'Acumuló 1000 puntos',
 'badge_hero.png',
 'PUNTOS',
 1000),

(5,'Colaborador Constante',
 'Participó durante varios días',
 'badge_collaborator.png',
 'ACTIVIDAD',
 30);
 
 INSERT INTO gaming_profile
(id, puntos_totales, usuario_id, nivel_id, created_at, updated_at)
VALUES
(1, 850, 1, 4, NOW(), NOW());

INSERT INTO reporte
(id,titulo,descripcion,latitud,longitud,direccion,estado,puntos_otorgados,usuario_id,created_at,updated_at)
VALUES

(1,
'Acumulación de basura',
'Gran cantidad de basura en la vía pública',
-12.0621061,
-77.1032456,
'Ventanilla - Callao',
'RESUELTO',
100,
1,
NOW(),
NOW()),

(2,
'Desmonte en avenida',
'Material de construcción abandonado',
-12.0598741,
-77.0958745,
'Av. Néstor Gambetta',
'RESUELTO',
100,
1,
NOW(),
NOW()),

(3,
'Basura en parque',
'Residuos sólidos acumulados',
-12.0554412,
-77.0998563,
'Parque Central Ventanilla',
'EN_PROCESO',
80,
1,
NOW(),
NOW()),

(4,
'Botadero informal',
'Vecinos arrojan residuos constantemente',
-12.0645123,
-77.1123654,
'AA.HH. Mi Perú',
'PENDIENTE',
0,
1,
NOW(),
NOW()),

(5,
'Residuos peligrosos',
'Baterías y químicos abandonados',
-12.0615478,
-77.1063254,
'Zona Industrial',
'RESUELTO',
120,
1,
NOW(),
NOW());
 
  INSERT INTO transaccion_puntos
(id,puntos,motivo,gaming_profile_id,reporte_id,created_at)
VALUES

(1,100,'Reporte resuelto',1,1,NOW()),
(2,100,'Reporte resuelto',1,2,NOW()),
(3,80,'Reporte en proceso',1,3,NOW()),
(4,120,'Reporte resuelto',1,5,NOW()),
(5,450,'Bonus participación',1,NULL,NOW());
  
INSERT INTO gaming_profile_badge
(id,gaming_profile_id,badge_id,obtenido_at)
VALUES

(1,1,1,NOW()),
(2,1,2,NOW()),
(3,1,3,NOW());
  
  INSERT INTO historial_estado
(id,estado_anterior,estado_nuevo,comentario,reporte_id,admin_id,created_at)
VALUES

(1,'PENDIENTE','EN_PROCESO',
'Reporte asignado',
1,
3,
NOW()),

(2,'EN_PROCESO','RESUELTO',
'Zona limpiada',
1,
3,
NOW()),

(3,'PENDIENTE','EN_PROCESO',
'Cuadrilla enviada',
2,
3,
NOW()),

(4,'EN_PROCESO','RESUELTO',
'Caso solucionado',
2,
3,
NOW()),

(5,'PENDIENTE','EN_PROCESO',
'Inspección realizada',
3,
3,
NOW()),

(6,'PENDIENTE','RESUELTO',
'Material retirado',
5,
3,
NOW());

#=============================================
#-----------------CUPON----------------------
#============================================
INSERT INTO cupon
(
nombre,
descripcion,
costo_puntos,
stock,
fecha_expiracion,
activo,
created_at
)
VALUES

(
'Descuento Starbucks',
'Cupón de 20% de descuento en bebidas seleccionadas',
100,
50,
'2026-12-31 23:59:59',
1,
NOW()
),

(
'Combo Burger Gratis',
'Canjeable por una hamburguesa clásica',
200,
30,
'2026-12-31 23:59:59',
1,
NOW()
),

(
'Vale Plaza Vea S/20',
'Descuento de S/20 en compras mayores a S/100',
300,
20,
'2026-12-31 23:59:59',
1,
NOW()
),

(
'Entrada Cine',
'Una entrada general para cine',
250,
40,
'2026-12-31 23:59:59',
1,
NOW()
),

(
'Gift Card S/50',
'Tarjeta de regalo valorizada en S/50',
500,
10,
'2026-12-31 23:59:59',
1,
NOW()
);

SELECT *
FROM gaming_profile
WHERE usuario_id = 1;

INSERT INTO canje
(
codigo_canje,
estado,
created_at,
gaming_profile_id,
cupon_id
)
VALUES

(
'CJ-2026-0001',
'ENTREGADO',
NOW(),
1,
1
),

(
'CJ-2026-0002',
'ENTREGADO',
NOW(),
1,
2
),

(
'CJ-2026-0003',
'PENDIENTE',
NOW(),
1,
3
),

(
'CJ-2026-0004',
'PENDIENTE',
NOW(),
1,
4
),

(
'CJ-2026-0005',
'CANCELADO',
NOW(),
1,
5
);

select * from usuario;
select * from nivel;
select * from historial_estado;
select * from gaming_profile;
select * from gaming_profile_badge;
select * from badge;