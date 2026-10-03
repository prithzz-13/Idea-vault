CREATE DATABASE IF NOT EXISTS idea_vault;
USE idea_vault;

CREATE TABLE users (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    email         VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(10)  NOT NULL DEFAULT 'STUDENT',
    department    VARCHAR(100),
    year_of_study INT,
    bio           VARCHAR(500)
);

CREATE TABLE skills (
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE user_skills (
    user_id  BIGINT NOT NULL,
    skill_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, skill_id),
    FOREIGN KEY (user_id)  REFERENCES users(id),
    FOREIGN KEY (skill_id) REFERENCES skills(id)
);

CREATE TABLE ideas (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    status      VARCHAR(10) NOT NULL DEFAULT 'OPEN',
    team_size   INT NOT NULL DEFAULT 4,
    owner_id    BIGINT NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (owner_id) REFERENCES users(id)
);

CREATE TABLE idea_skills (
    idea_id  BIGINT NOT NULL,
    skill_id BIGINT NOT NULL,
    PRIMARY KEY (idea_id, skill_id),
    FOREIGN KEY (idea_id)  REFERENCES ideas(id),
    FOREIGN KEY (skill_id) REFERENCES skills(id)
);

CREATE TABLE join_requests (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    idea_id      BIGINT NOT NULL,
    requester_id BIGINT NOT NULL,
    message      VARCHAR(300),
    status       VARCHAR(10) NOT NULL DEFAULT 'PENDING',
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (idea_id)      REFERENCES ideas(id),
    FOREIGN KEY (requester_id) REFERENCES users(id),
    UNIQUE (idea_id, requester_id)
);

CREATE TABLE mentor_requests (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    mentor_id  BIGINT NOT NULL,
    idea_id    BIGINT,
    message    VARCHAR(300),
    status     VARCHAR(10) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES users(id),
    FOREIGN KEY (mentor_id)  REFERENCES users(id),
    FOREIGN KEY (idea_id)    REFERENCES ideas(id)
);