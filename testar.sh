#!/usr/bin/env bash
# Roda os testes automáticos. Antes, execute no MySQL o schema.sql e o dados_teste.sql.
set -e
cd "$(dirname "$0")"
VERSAO=8.4.0
JAR="lib/mysql-connector-j-$VERSAO.jar"
mkdir -p lib out out_test
if [ ! -f "$JAR" ]; then
  curl -L -o "$JAR" "https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/$VERSAO/mysql-connector-j-$VERSAO.jar"
fi
javac --release 17 -encoding UTF-8 -d out $(find src/main/java -name '*.java')
javac --release 17 -encoding UTF-8 -cp out -d out_test src/test/java/TestesSistema.java
java -Dstdout.encoding=UTF-8 -cp "out:out_test:$JAR" TestesSistema
