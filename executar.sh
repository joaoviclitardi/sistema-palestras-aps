#!/usr/bin/env bash
# Compila e executa o sistema (Linux/macOS). Requisitos: Java 17+ e MySQL 8 ligado,
# com o banco criado pelo script src/main/resources/sql/schema.sql.
set -e
cd "$(dirname "$0")"
VERSAO=8.4.0
JAR="lib/mysql-connector-j-$VERSAO.jar"
mkdir -p lib out
if [ ! -f "$JAR" ]; then
  echo "Baixando o MySQL Connector/J $VERSAO..."
  curl -L -o "$JAR" "https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/$VERSAO/mysql-connector-j-$VERSAO.jar"
fi
javac --release 17 -encoding UTF-8 -d out $(find src/main/java -name '*.java')
java -cp "out:$JAR" Main
