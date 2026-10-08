# Sistema de Palestras e Inscrições em Eventos Acadêmicos

APS 2026/2, Sistemas de Informação, UNIP.
Sistema em Java (Swing) com MySQL para cadastrar palestras e inscrever alunos.

Equipe: Julia Helena Moreira, João Victor Litardi e Caio Rafael.

## O que o sistema faz

- **Área do Aluno:** o aluno digita o nome completo, consulta as palestras (com busca, filtro por professor e ordenação), se inscreve e vê ou cancela as próprias inscrições.
- **Área do Coordenador:** cadastra, altera, exclui, busca, filtra e ordena alunos, palestras, professores, cursos e inscrições.
- O sistema não deixa o aluno se inscrever duas vezes na mesma palestra e não deixa excluir registros que estão em uso (por exemplo, um curso que tem alunos).

## Tecnologias

Java 17, Swing, padrão MVC com DAO, JDBC, MySQL 8.

## Como rodar

1. Instale o Java 17 ou mais novo e o MySQL 8 (com o MySQL Workbench).
2. No MySQL Workbench, abra e execute (raio amarelo) os dois arquivos, nesta ordem:
   - `src/main/resources/sql/schema.sql` (cria o banco `palestras_db` e as tabelas)
   - `src/main/resources/sql/dados_teste.sql` (insere cursos, professores, alunos, palestras e inscrições)
3. Abra o arquivo `src/main/java/dao/ConexaoDB.java` e confira se `USUARIO` e `SENHA` são os do seu MySQL. O padrão é `root` e `root`.
4. Baixe o driver do MySQL (Connector/J) em https://dev.mysql.com/downloads/connector/j/ (opção "Platform Independent", arquivo `.jar`).
5. Execute o projeto de uma destas formas:
   - **NetBeans, Eclipse ou IntelliJ:** abra a pasta do projeto, adicione o `.jar` do Connector/J às bibliotecas do projeto e execute a classe `Main`.
   - **Pelo terminal:** rode `executar.bat` (Windows) ou `./executar.sh` (Linux/macOS). O script baixa o driver sozinho.

Para entrar como aluno, use um nome da lista, por exemplo `Julia Moreira`.

## Testes automáticos

Depois de executar os dois scripts SQL, rode `testar.bat`/`./testar.sh` ou execute a classe `TestesSistema` (pasta `src/test/java`) com o Connector/J nas bibliotecas. Os testes criam registros de teste e apagam tudo no final. São 48 casos. A saída mostra PASSOU ou FALHOU para cada um.

## Estrutura

```
src/main/java
  Main.java            ponto de entrada
  model/               classes do domínio (Curso, Professor, Aluno, Palestra, Inscricao)
  dao/                 acesso ao banco com JDBC (um DAO por tabela e ConexaoDB)
  controller/          regras e validações, chamadas pelas telas
  view/                telas Swing
  util/                Validador, SistemaException e ErroSql
src/main/resources/sql
  schema.sql           criação do banco
  dados_teste.sql      dados de teste
  consultas.sql        consultas com JOIN, filtro e ordenação
src/test/java          testes automáticos
docs/                  diagramas (DER, UML e arquitetura)
```

## Fluxo de uma ação

Tela (view) chama o controller, que valida os dados e chama o DAO, que executa o SQL no MySQL. A tela nunca escreve SQL.
