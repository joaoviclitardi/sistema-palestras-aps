-- ============================================================
-- Sistema de Palestras e Inscrições em Eventos Acadêmicos
-- APS 2026/2 - UNIP - Script de criação do banco (MySQL 8)
-- ============================================================

DROP DATABASE IF EXISTS palestras_db;
CREATE DATABASE palestras_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE palestras_db;

-- Curso: classifica o aluno
CREATE TABLE curso (
    id_curso   INT          NOT NULL AUTO_INCREMENT,
    nome_curso VARCHAR(100) NOT NULL,
    CONSTRAINT pk_curso PRIMARY KEY (id_curso),
    CONSTRAINT uk_curso_nome UNIQUE (nome_curso)
) ENGINE=InnoDB;

-- Professor: responsável por ministrar palestras
CREATE TABLE professor (
    id_professor INT          NOT NULL AUTO_INCREMENT,
    nome         VARCHAR(120) NOT NULL,
    email        VARCHAR(120) NOT NULL,
    CONSTRAINT pk_professor PRIMARY KEY (id_professor),
    CONSTRAINT uk_professor_email UNIQUE (email)
) ENGINE=InnoDB;

-- Aluno: pertence a exatamente um curso
CREATE TABLE aluno (
    id_aluno INT          NOT NULL AUTO_INCREMENT,
    nome     VARCHAR(120) NOT NULL,
    ra       VARCHAR(20)  NOT NULL,
    email    VARCHAR(120) NOT NULL,
    id_curso INT          NOT NULL,
    CONSTRAINT pk_aluno PRIMARY KEY (id_aluno),
    CONSTRAINT uk_aluno_ra UNIQUE (ra),
    CONSTRAINT uk_aluno_email UNIQUE (email),
    CONSTRAINT fk_aluno_curso FOREIGN KEY (id_curso)
        REFERENCES curso (id_curso)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- Palestra: possui exatamente um professor responsável
CREATE TABLE palestra (
    id_palestra  INT          NOT NULL AUTO_INCREMENT,
    titulo       VARCHAR(150) NOT NULL,
    descricao    TEXT,
    data         DATE         NOT NULL,
    horario      TIME         NOT NULL,
    local        VARCHAR(100) NOT NULL,
    id_professor INT          NOT NULL,
    CONSTRAINT pk_palestra PRIMARY KEY (id_palestra),
    CONSTRAINT fk_palestra_professor FOREIGN KEY (id_professor)
        REFERENCES professor (id_professor)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- Inscrição: tabela associativa entre aluno e palestra
CREATE TABLE inscricao (
    id_inscricao   INT  NOT NULL AUTO_INCREMENT,
    id_aluno       INT  NOT NULL,
    id_palestra    INT  NOT NULL,
    data_inscricao DATE NOT NULL,
    CONSTRAINT pk_inscricao PRIMARY KEY (id_inscricao),
    CONSTRAINT uk_inscricao UNIQUE (id_aluno, id_palestra),
    CONSTRAINT fk_inscricao_aluno FOREIGN KEY (id_aluno)
        REFERENCES aluno (id_aluno)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_inscricao_palestra FOREIGN KEY (id_palestra)
        REFERENCES palestra (id_palestra)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_palestra_data ON palestra (data, horario);
CREATE INDEX idx_aluno_nome ON aluno (nome);
