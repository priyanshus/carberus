-- init.sql
CREATE TABLE system_users (
  id SERIAL PRIMARY KEY,
  email TEXT UNIQUE NOT NULL,
  hashed_password TEXT NOT NULL,
  first_name TEXT,
  last_name TEXT,
  role VARCHAR(50) CHECK (role IN ('ADMIN', 'NONADMIN', 'VIEWER')) DEFAULT 'ADMIN',
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_login TIMESTAMP
);

CREATE TABLE projects (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    project_code VARCHAR(4) NOT NULL,
    status VARCHAR(20) CHECK (status IN ('ACTIVE', 'ARCHIVED')) DEFAULT 'ACTIVE',
    created_by BIGINT REFERENCES system_users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE project_members (
    project_id BIGINT REFERENCES projects(id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES system_users(id) ON DELETE CASCADE,
    project_role VARCHAR(50) CHECK (project_role IN ('OWNER', 'MANAGER', 'TESTER', 'VIEWER')) NOT NULL,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (project_id, user_id)
);



