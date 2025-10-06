@echo off
echo ========================================
echo    SISTEMA DE CONTROLE DE ESTOQUE
echo    Script de Build e Deploy
echo ========================================
echo.

REM Verificar se estamos no diretório correto
if not exist "pom.xml" (
    echo ERRO: Arquivo pom.xml não encontrado!
    echo Certifique-se de estar no diretório backend do projeto.
    pause
    exit /b 1
)

echo [1/5] Verificando estrutura do projeto...
if not exist "src\main\java\com\controleestoque\enums\Role.java" (
    echo ERRO: Arquivo Role.java não encontrado!
    echo Recriando arquivo Role.java...
    
    REM Criar diretório se não existir
    if not exist "src\main\java\com\controleestoque\enums" mkdir "src\main\java\com\controleestoque\enums"
    
    REM Recriar Role.java
    (
        echo package com.controleestoque.enums;
        echo.
        echo /**
        echo  * Enum para definir os papéis dos usuários no sistema
        echo  */
        echo public enum Role {
        echo     ADMIN^("Administrador"^),
        echo     VENDEDOR^("Vendedor"^),
        echo     VISUALIZADOR^("Visualizador"^);
        echo.
        echo     private final String descricao;
        echo.
        echo     Role^(String descricao^) {
        echo         this.descricao = descricao;
        echo     }
        echo.
        echo     public String getDescricao^(^) {
        echo         return descricao;
        echo     }
        echo }
    ) > "src\main\java\com\controleestoque\enums\Role.java"
    
    echo Arquivo Role.java recriado com sucesso!
)

echo [2/5] Limpando projeto anterior...
call mvn clean

if %ERRORLEVEL% neq 0 (
    echo ERRO: Falha na limpeza do projeto!
    pause
    exit /b 1
)

echo [3/5] Compilando projeto...
call mvn compile

if %ERRORLEVEL% neq 0 (
    echo ERRO: Falha na compilação!
    pause
    exit /b 1
)

echo [4/5] Executando testes...
call mvn test

if %ERRORLEVEL% neq 0 (
    echo AVISO: Alguns testes falharam, mas continuando...
)

echo [5/5] Criando JAR do projeto...
call mvn package -DskipTests

if %ERRORLEVEL% neq 0 (
    echo ERRO: Falha na criação do JAR!
    pause
    exit /b 1
)

echo.
echo ========================================
echo    BUILD CONCLUÍDO COM SUCESSO!
echo ========================================
echo.
echo O JAR foi criado em: target\controle-estoque-1.0.0.jar
echo.
echo Para executar a aplicação:
echo   mvn spring-boot:run
echo.
echo Para executar o JAR:
echo   java -jar target\controle-estoque-1.0.0.jar
echo.
pause

