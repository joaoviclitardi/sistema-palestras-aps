@echo off
REM Compila e executa o Sistema de Palestras (Windows).
REM Requisitos: Java 17+ e MySQL 8 ligado, com o banco criado pelo script schema.sql.
cd /d "%~dp0"
set VERSAO=8.4.0
set JAR=lib\mysql-connector-j-%VERSAO%.jar
if not exist lib mkdir lib
if not exist out mkdir out
if not exist %JAR% (
  echo Baixando o MySQL Connector/J %VERSAO%...
  curl -L -o %JAR% https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/%VERSAO%/mysql-connector-j-%VERSAO%.jar
)
dir /s /b src\main\java\*.java > fontes.txt
javac --release 17 -encoding UTF-8 -d out @fontes.txt
java -cp "out;%JAR%" Main
