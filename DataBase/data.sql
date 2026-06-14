INSERT INTO rol (nombre, descripcion) VALUES
  ('ROLE_ADMIN', 'Funcionario municipal que gestiona reportes'),
  ('ROLE_CIUDADANO', 'Ciudadano que crea y hace seguimiento de reportes');

INSERT INTO nivel (nombre, descripcion, puntos_minimos, icono) VALUES
  ('Principiante', 'Recién unido a la causa', 0, '🌱'),
  ('Reportero', 'Ya tienes varios reportes enviados', 100, '📋'),
  ('Guardián urbano', 'Defensor activo de tu ciudad', 300, '🛡️'),
  ('Héroe de la ciudad', 'Referente de la comunidad', 700, '🏆');