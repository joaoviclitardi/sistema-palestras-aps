@echo off
REM Roda os testes automaticos. Antes, execute no MySQL o schema.sql e o dados_teste.sql.
cd /d "%~dp0"
set VERSAO=8.4.0
set JAR=lib\mysql-connector-j-%VERSAO%.jar
if not exist lib mkdir lib
if not exist out mkdir out
if not exist out_test mkdir out_test
if not exist %JAR% (
  curl -L -o %JAR% https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/%VERSAO%/mysql-connector-j-%VERSAO%.jar
)
dir /s /b src\main\java\*.java > fontes.txt
javac --release 17 -encoding UTF-8 -d out @fontes.txt
javac --release 17 -encoding UTF-8 -cp out -d out_test src\test\java\TestesSistema.java
java -Dstdout.encoding=UTF-8 -cp "out;out_test;%JAR%" TestesSistema
