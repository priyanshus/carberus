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
  '$2a$10$WUe2kJ8C8z58ugymsBJD6.cku52PNQ4juhHW5KCrSq8XA1pp42uY6',
  'Admin',
  'User',
  'ADMIN',
  TRUE,
  CURRENT_TIMESTAMP,
  CURRENT_TIMESTAMP,
  NULL
);

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
  'tester1@example.com',
  '$2a$10$WUe2kJ8C8z58ugymsBJD6.cku52PNQ4juhHW5KCrSq8XA1pp42uY6',
  'Tester',
  'User1',
  'NONADMIN',
  TRUE,
  CURRENT_TIMESTAMP,
  CURRENT_TIMESTAMP,
  NULL
);

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
  'tester2@example.com',
  '$2a$10$WUe2kJ8C8z58ugymsBJD6.cku52PNQ4juhHW5KCrSq8XA1pp42uY6',
  'Tester',
  'User2',
  'NONADMIN',
  TRUE,
  CURRENT_TIMESTAMP,
  CURRENT_TIMESTAMP,
  NULL
);
