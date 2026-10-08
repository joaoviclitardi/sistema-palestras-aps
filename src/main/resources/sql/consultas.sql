-- ============================================================
-- Consultas de apoio (JOIN, filtro, ordenação e agregação)
-- ============================================================
USE palestras_db;

-- 1) Listar todas as palestras com o professor responsável (JOIN)
SELECT p.id_palestra, p.titulo, p.data, p.horario, p.local, pr.nome AS professor
FROM palestra p
JOIN professor pr ON pr.id_professor = p.id_professor;

-- 2) Palestras ordenadas por data, horário e título (múltiplos critérios)
SELECT p.titulo, p.data, p.horario
FROM palestra p
ORDER BY p.data, p.horario, p.titulo;

-- 3) Busca por texto no título ou na descrição (filtro)
SELECT p.id_palestra, p.titulo
FROM palestra p
WHERE p.titulo LIKE '%Java%' OR p.descricao LIKE '%Java%'
ORDER BY p.titulo;

-- 4) Palestras de um professor a partir de uma data (filtro composto)
SELECT p.titulo, p.data, p.horario
FROM palestra p
WHERE p.id_professor = 2 AND p.data >= '2026-11-10'
ORDER BY p.data, p.horario;

-- 5) Alunos com o nome do curso, ordenados por curso e nome
SELECT a.id_aluno, a.nome, a.ra, a.email, c.nome_curso
FROM aluno a
JOIN curso c ON c.id_curso = a.id_curso
ORDER BY c.nome_curso, a.nome;

-- 6) Inscrições com aluno, palestra e professor (JOIN de cinco tabelas)
SELECT i.id_inscricao, a.nome AS aluno, a.ra, p.titulo AS palestra,
       p.data, p.horario, pr.nome AS professor, i.data_inscricao
FROM inscricao i
JOIN aluno a      ON a.id_aluno = i.id_aluno
JOIN palestra p   ON p.id_palestra = i.id_palestra
JOIN professor pr ON pr.id_professor = p.id_professor
ORDER BY p.data, p.horario, a.nome;

-- 7) Inscrições de um aluno específico
SELECT p.titulo, p.data, p.horario, p.local
FROM inscricao i
JOIN palestra p ON p.id_palestra = i.id_palestra
JOIN aluno a    ON a.id_aluno = i.id_aluno
WHERE a.nome = 'Julia Moreira'
ORDER BY p.data, p.horario;

-- 8) Quantidade de inscritos por palestra (agregação)
SELECT p.titulo, COUNT(i.id_inscricao) AS inscritos
FROM palestra p
LEFT JOIN inscricao i ON i.id_palestra = p.id_palestra
GROUP BY p.id_palestra, p.titulo
ORDER BY inscritos DESC, p.titulo;

-- 9) Palestras sem nenhuma inscrição
SELECT p.titulo
FROM palestra p
LEFT JOIN inscricao i ON i.id_palestra = p.id_palestra
WHERE i.id_inscricao IS NULL;

-- 10) Quantidade de palestras por professor
SELECT pr.nome, COUNT(p.id_palestra) AS palestras
FROM professor pr
LEFT JOIN palestra p ON p.id_professor = pr.id_professor
GROUP BY pr.id_professor, pr.nome
ORDER BY palestras DESC, pr.nome;

-- 11) Alunos ainda sem inscrição
SELECT a.nome, a.ra
FROM aluno a
LEFT JOIN inscricao i ON i.id_aluno = a.id_aluno
WHERE i.id_inscricao IS NULL
ORDER BY a.nome;
