-- V2__AddUsers.sql
INSERT INTO system_users (
  email,
  hashed_password,
  first_name,
  last_name,
  role,
  is_active,
  created_at,
  updated_at,
  last_login
) VALUES (
  'admin@example.com',
  '$2a$12$O2IM7D/hnYXjuWhf4uPiFOytCk3SS5XsMxBRuSn3mp0nHuKE1NH/y', -- hashed 'admin@123'
  'Admin',
  'User',
  'admin',
  TRUE,
  CURRENT_TIMESTAMP,
  CURRENT_TIMESTAMP,
  NULL
);
